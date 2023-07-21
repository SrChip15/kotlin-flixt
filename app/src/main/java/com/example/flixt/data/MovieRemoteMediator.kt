package com.example.flixt.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.flixt.BuildConfig
import com.example.flixt.data.database.DatabaseMovie
import com.example.flixt.data.database.MovieDatabase
import com.example.flixt.data.database.RemoteKeys
import com.example.flixt.network.TmdbApiService
import com.example.flixt.network.asDatabaseModel
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale
import kotlin.properties.Delegates

private const val STARTING_PAGE = 1

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val service: TmdbApiService,
    private val database: MovieDatabase,
) : RemoteMediator<Int, DatabaseMovie>() {

    private var totalPages by Delegates.notNull<Int>()
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DatabaseMovie>
    ): MediatorResult {

        Log.i("MovieMediator#load", "${"load".uppercase(Locale.ROOT)} called")

        val page = when (loadType) {
            LoadType.REFRESH -> {
                Log.i("MovieMediator#load", "${loadType.name} triggered")
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE
            }

            LoadType.PREPEND -> {
                Log.i("MovieMediator#load", "${loadType.name} triggered")
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                Log.i("MovieMediator#load", "Prev Key: $prevKey")
                prevKey
            }

            LoadType.APPEND -> {
                Log.i("MovieMediator#load", "${loadType.name} triggered")
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                Log.i("MovieMediator#load", "Next Key: $nextKey")
                nextKey
            }
        }

        try {
            Log.i("MovieMediator#load", "Page requested from API #$page")
            val response = service.getMovies(BuildConfig.API_KEY, page)
            val movies = response.movies
            val endOfPaginationReached = page >= response.totalPages

            Log.i("MovieMediator#load", "${movies.size} movies fetched from API")
            Log.i("MovieMediator#load", "End Of Pagination: $endOfPaginationReached")
            // movies.map {
            //     Log.i(
            //         "MovieRemoteMediator",
            //         "From Tmdb Request -> #${it.id}_${it.title}"
            //     )
            // }

            database.withTransaction {
                // Clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    totalPages = response.totalPages
                    database.remoteKeysDao().clearRemoteKeys()
                    database.movieDao().clearMovies()
                }

                val prevKey = if (page == STARTING_PAGE) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = movies.map { movie ->
                    RemoteKeys(movieId = movie.id, prevKey = prevKey, nextKey = nextKey)
                }

                database.remoteKeysDao().insertAll(keys)
                database.movieDao().insertAll(movies.asDatabaseModel()).also {
                    Log.i(
                        "MovieMediator#load",
                        "Movies saved to Db"
                    )
                }
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, DatabaseMovie>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { movieId ->
                database.remoteKeysDao().remoteKeysMovieId(movieId)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, DatabaseMovie>): RemoteKeys? {
        val firstLoadedPage = state.pages.firstOrNull()
        val prevKeyFromDb = firstLoadedPage?.prevKey

        return if ((prevKeyFromDb != null) && (prevKeyFromDb >= STARTING_PAGE)) {
            firstLoadedPage.data.firstOrNull()?.let { movie ->
                database.remoteKeysDao().remoteKeysMovieId(movie.id)
            }
        } else {
            null
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, DatabaseMovie>): RemoteKeys? {
        val lastLoadedPage = state.pages.lastOrNull()
        val nextKeyFromDb = lastLoadedPage?.nextKey

        return if ((nextKeyFromDb != null) && (nextKeyFromDb < totalPages)) {
            lastLoadedPage.data.lastOrNull()?.let { movie ->
                database.remoteKeysDao().remoteKeysMovieId(movie.id)
            }
        } else {
            null
        }
    }
}
