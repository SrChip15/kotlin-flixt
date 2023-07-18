package com.example.flixt.data

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

private const val STARTING_PAGE = 1

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val service: TmdbApiService,
    private val database: MovieDatabase,
) : RemoteMediator<Int, DatabaseMovie>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DatabaseMovie>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        return try {
            val response = service.getMovies(BuildConfig.API_KEY, page)
            val movies = response.movies
            val endOfPaginationReached = movies.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.movieDao().clearMovies()
                }
                val prevKey = if (page == STARTING_PAGE) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = movies.map { movie ->
                    RemoteKeys(movieId = movie.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeysDao().insertAll(keys)
                database.movieDao().insertAll(movies.asDatabaseModel())
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
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
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { movie ->
            database.remoteKeysDao().remoteKeysMovieId(movie.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, DatabaseMovie>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { movie ->
            database.remoteKeysDao().remoteKeysMovieId(movie.id)
        }
    }
}
