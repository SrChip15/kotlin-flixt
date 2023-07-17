package com.example.flixt

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.flixt.domain.Movie
import com.example.flixt.network.TmdbApiService.Companion.POSTER_IMAGE_BASE_URL
import com.example.flixt.network.TmdbApiService.Companion.POSTER_IMAGE_SIZE


@BindingAdapter("imgUrl")
fun bindMoviePoster(imgView: ImageView, movie: Movie) {
    val imgUrl = movie.posterPath
    val contentDescription = movie.title + " movie poster"

    imgUrl.let {
        val imgUri =
            (POSTER_IMAGE_BASE_URL + POSTER_IMAGE_SIZE + it).toUri().buildUpon().scheme("https")
                .build()
        Glide.with(imgView.context)
            .load(imgUri)
            .into(imgView)

        imgView.contentDescription = contentDescription
    }
}
