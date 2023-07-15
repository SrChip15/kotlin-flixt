package com.example.flixt

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.flixt.network.TmdbApiService.Companion.POSTER_IMAGE_BASE_URL
import com.example.flixt.network.TmdbApiService.Companion.POSTER_IMAGE_SIZE

// @BindingAdapter("listData")
// fun bindRecyclerView(recyclerView: RecyclerView, data: List<Movie>?) {
//     val adapter = recyclerView.adapter as MovieGridAdapter
//     adapter.submitData(data)
// }


@BindingAdapter("imgUrl")
fun bindMoviePoster(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri =
            (POSTER_IMAGE_BASE_URL + POSTER_IMAGE_SIZE + it).toUri().buildUpon().scheme("https")
                .build()
        Glide.with(imgView.context)
            .load(imgUri)
            .into(imgView)
    }
}
