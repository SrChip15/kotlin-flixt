package com.example.flixt.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.flixt.R
import com.example.flixt.databinding.FragmentOverviewBinding
import com.example.flixt.network.TmdbApi
import com.example.flixt.repository.MovieRepository
import kotlinx.coroutines.launch

class OverviewFragment : Fragment() {

    private lateinit var binding: FragmentOverviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val movieRepository = MovieRepository(TmdbApi.retrofitService)
        val viewModelFactory = OverviewViewModelFactory(movieRepository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[OverviewViewModel::class.java]
        val movieGridAdapter = MovieGridAdapter()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false)

        binding.apply {

            movieList.adapter = movieGridAdapter
            binding.movieList.setHasFixedSize(true)

            lifecycleOwner = viewLifecycleOwner
            overviewViewModel = viewModel


            // viewModel.movies.observe(this@OverviewFragment.viewLifecycleOwner) {
            //     movieGridAdapter.submitData(it)
            // }

            lifecycleScope.launch {
                viewModel.getMoviesFromApi()
                    .observe(this@OverviewFragment.viewLifecycleOwner) { movies ->
                        movies?.let {
                            viewLifecycleOwner.lifecycleScope.launch {
                                movieGridAdapter.submitData(lifecycle, movies)
                            }
                        }
                    }
            }
        }

        return binding.root
    }
}