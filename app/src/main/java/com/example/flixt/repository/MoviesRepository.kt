package com.example.flixt.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.flixt.data.paging.MoviePagingSource
import com.example.flixt.domain.Movie
import com.example.flixt.network.TmdbApiService
import com.example.flixt.network.asDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MoviesRepository(
    private val service: TmdbApiService
) {

    // TODO: Use RemoteMediator

    fun getMoviesStream(): Flow<PagingData<Movie>> {
        // Pager is the class responsible for producing the PagingData stream.
        // It depends on the PagingSource to do this.
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviePagingSource(service) }
        )
            .flow
            .map { pagingData ->
                pagingData.map { it.asDomainModel() }
            }
    }
}