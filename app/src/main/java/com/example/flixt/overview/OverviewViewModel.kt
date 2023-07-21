package com.example.flixt.overview

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.flixt.data.database.MovieDatabase
import com.example.flixt.network.TmdbApi
import com.example.flixt.repository.MoviesRepository

class OverviewViewModel(application: Application) : ViewModel() {

    private val service = TmdbApi.retrofitService
    private val database = MovieDatabase.getInstance(application)
    private val repository = MoviesRepository(service, database)

    val movies = repository.getMoviesStream().asLiveData().cachedIn(viewModelScope)

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return OverviewViewModel(app) as T
            }

            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
