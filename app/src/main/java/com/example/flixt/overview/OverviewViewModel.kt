package com.example.flixt.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.flixt.network.TmdbApi
import com.example.flixt.repository.MoviesRepository

class OverviewViewModel : ViewModel() {

    private val service = TmdbApi.retrofitService
    private val repository = MoviesRepository(service)

    // cachedIn allows the paged data to remain active in the viewModel scope
    // so, even if the UI were to go through lifecycle changes, the paged data would remain in cache
    // and the UI does not have to start paging from the beginning when it resumes
    val movies = repository.getMoviesStream().asLiveData().cachedIn(viewModelScope)
}
