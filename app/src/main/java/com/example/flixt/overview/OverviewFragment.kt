package com.example.flixt.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.flixt.databinding.FragmentOverviewBinding

class OverviewFragment : Fragment() {

    private val viewModel: OverviewViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "ViewModel cannot be accessed before fragment is created"
        }
        val factory = OverviewViewModel.Factory(activity.application)
        ViewModelProvider(this, factory)[OverviewViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentOverviewBinding.inflate(inflater)

        binding.movieList.adapter = MovieGridAdapter()
        binding.movieList.setHasFixedSize(true)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.overviewViewModel = viewModel

        return binding.root
    }
}