package com.example.flixt.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.flixt.databinding.FragmentOverviewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OverviewFragment : Fragment() {

    private val viewModel: OverviewViewModel by lazy {
        ViewModelProvider(this)[OverviewViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentOverviewBinding.inflate(inflater)

        val movieGridAdapter = MovieGridAdapter()

        binding.movieList.adapter = movieGridAdapter
        binding.movieList.setHasFixedSize(true)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.overviewViewModel = viewModel

        lifecycleScope.launch {
            viewModel.movies.collectLatest {
                movieGridAdapter.submitData(it)
            }
        }

        return binding.root
    }
}