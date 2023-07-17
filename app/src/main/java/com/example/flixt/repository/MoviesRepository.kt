package com.example.flixt.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.flixt.BuildConfig
import com.example.flixt.database.MovieDatabase
import com.example.flixt.database.asDomainModel
import com.example.flixt.domain.Movie
import com.example.flixt.network.TmdbApi
import com.example.flixt.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoviesRepository(private val database: MovieDatabase) {

    val movies: LiveData<List<Movie>> = database.movieDao.getMovies().map {
        it.asDomainModel()
    }

    suspend fun refreshMovies() {
        withContext(Dispatchers.IO) {
            val response = TmdbApi.retrofitService.getMovies(BuildConfig.API_KEY, 1)
            database.movieDao.insertAll(*response.movies.asDatabaseModel())
        }
    }
}