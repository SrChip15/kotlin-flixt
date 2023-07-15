package com.example.flixt.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.flixt.network.Movie
import com.example.flixt.network.TmdbApiService
import kotlinx.coroutines.flow.Flow

class MovieRepository(private val apiService: TmdbApiService) {

    fun getMovies(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MovieListPagingSource(apiService) },
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 20
    }
}


