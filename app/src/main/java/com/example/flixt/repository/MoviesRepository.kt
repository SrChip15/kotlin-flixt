package com.example.flixt.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.flixt.BuildConfig
import com.example.flixt.data.MovieRemoteMediator
import com.example.flixt.data.database.DatabaseMovie
import com.example.flixt.data.database.MovieDatabase
import com.example.flixt.network.TmdbApiService
import com.example.flixt.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MoviesRepository(
    private val service: TmdbApiService,
    private val database: MovieDatabase
) {

    // TODO: Use RemoteMediator
    private val pagingSourceFactory = { database.movieDao().getMovies() }

    suspend fun refreshMovies() {
        withContext(Dispatchers.IO) {
            val response = service.getMovies(BuildConfig.API_KEY, 1)
            database.movieDao().insertAll(response.movies.asDatabaseModel())
        }
    }

    fun getMoviesStream(): Flow<PagingData<DatabaseMovie>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = MovieRemoteMediator(
                service,
                database,
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}