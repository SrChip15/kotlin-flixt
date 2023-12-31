package com.example.flixt.network

import android.os.Parcelable
import com.example.flixt.domain.Movie
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

data class DiscoverResponse(
    @Json(name = "results") val movies: List<NetworkMovie>,
    val page: Int,
    @Json(name = "total_results") val totalResults: Int,
    @Json(name = "total_pages") val totalPages: Int,
)

@Parcelize
data class NetworkMovie(
    val id: Int,
    @Json(name = "vote_count") val voteCount: Int,
    val video: Boolean,
    @Json(name = "vote_average") val voteAverage: Double,
    val title: String,
    val popularity: Double,
    @Json(name = "poster_path") val posterPath: String,
    @Json(name = "original_title") val originalTitle: String,
    @Json(name = "backdrop_path") val backdropPath: String?,
    val overview: String,
    @Json(name = "release_date") val releaseDate: String,
) : Parcelable

fun NetworkMovie.asDomainModel(): Movie {
    return Movie(
        id = id,
        title = title,
        posterPath = posterPath,
        overview = overview,
    )
}
