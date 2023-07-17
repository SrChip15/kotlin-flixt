package com.example.flixt.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.flixt.BuildConfig
import com.example.flixt.data.database.MovieDatabase
import com.example.flixt.data.paging.MoviePagingSource
import com.example.flixt.domain.Movie
import com.example.flixt.network.TmdbApiService
import com.example.flixt.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoviesRepository(
    private val service: TmdbApiService,
    private val database: MovieDatabase
) {

    // TODO: Use RemoteMediator

    suspend fun refreshMovies() {
        withContext(Dispatchers.IO) {
            val response = service.getMovies(BuildConfig.API_KEY, 1)
            database.movieDao.insertAll(*response.movies.asDatabaseModel())
        }
    }

    fun getMoviesStream(): LiveData<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviePagingSource(service) }
        ).liveData
    }
}