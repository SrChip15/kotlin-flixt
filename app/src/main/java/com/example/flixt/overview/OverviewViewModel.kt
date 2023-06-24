package com.example.flixt.overview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flixt.BuildConfig
import com.example.flixt.network.Movie
import com.example.flixt.network.TmdbApi
import kotlinx.coroutines.launch

class OverviewViewModel: ViewModel() {

    private val _movies = MutableLiveData<List<Movie>>()


    val movies: LiveData<List<Movie>>
        get() = _movies

    init {
        getMoviesFromApi()
    }

    private fun getMoviesFromApi() {
        viewModelScope.launch {
            val response = TmdbApi.retrofitService.getMovies(BuildConfig.API_KEY, 1)
            Log.i("OverviewVM", "response: $response")
            _movies.value = response.results
        }
    }
}