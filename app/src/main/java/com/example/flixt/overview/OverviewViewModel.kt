package com.example.flixt.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.flixt.network.TmdbApi
import com.example.flixt.repository.MoviesRepository

class OverviewViewModel : ViewModel() {

    private val service = TmdbApi.retrofitService
    private val repository = MoviesRepository(service)

    val movies = repository.getMoviesStream().cachedIn(viewModelScope)
}
