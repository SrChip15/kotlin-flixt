package com.example.flixt.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flixt.repository.MovieRepository

class OverviewViewModelFactory(private val movieRepository: MovieRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
            return OverviewViewModel(movieRepository) as T
        }
        throw IllegalArgumentException("Unknown viewmodel provided")
    }
}