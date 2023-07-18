package com.example.flixt.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.flixt.databinding.GridListItemBinding
import com.example.flixt.domain.Movie

class MovieGridAdapter :
    PagingDataAdapter<Movie, MovieGridAdapter.MoviePosterHolder>(MovieListDiffCallback) {
    class MoviePosterHolder private constructor(private val binding: GridListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Movie) {
            binding.movie = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(view: ViewGroup): MoviePosterHolder {
                val layoutInflater = LayoutInflater.from(view.context)
                val binding = GridListItemBinding.inflate(layoutInflater, view, false)
                return MoviePosterHolder(binding)
            }
        }
    }

    companion object MovieListDiffCallback: DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviePosterHolder {
        return MoviePosterHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MoviePosterHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

}