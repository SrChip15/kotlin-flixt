package com.example.flixt.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.flixt.R
import com.example.flixt.databinding.FragmentOverviewBinding

class OverviewFragment : Fragment() {

    private lateinit var binding: FragmentOverviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val viewModel = ViewModelProvider(this)[OverviewViewModel::class.java]

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false)

        binding.movieList.adapter = MovieGridAdapter()
        binding.movieList.setHasFixedSize(true)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.overviewViewModel = viewModel

        return binding.root
    }
}