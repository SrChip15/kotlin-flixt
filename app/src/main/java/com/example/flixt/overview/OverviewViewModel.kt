package com.example.flixt.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.flixt.network.Movie
import com.example.flixt.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class OverviewViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    // private val _movies = MutableLiveData<PagingData<Movie>>()
    //
    // val movies: LiveData<PagingData<Movie>>
    //     get() = _movies

    // init {
    //     getMoviesFromApi()
    // }

    private fun getMoviesFromApi(): Flow<PagingData<Movie>> =
        movieRepository.getMovies().cachedIn(viewModelScope)

    sealed class UiAction {
        data class Scroll(
            val visibleItemCount: Int,
            val lastVisibleItemPosition: Int,
            val totalItemCount: Int,
        ) : UiAction()
    }
}