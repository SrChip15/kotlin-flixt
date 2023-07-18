package com.example.flixt.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.flixt.domain.Movie

@Entity
data class DatabaseMovie constructor(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String,
    val backdropPath: String,
    val releaseDate: String,
    val video: Boolean,
    val popularity: Double,
    val voteCount: Int,
    val voteAverage: Double,
    val originalTitle: String,
    val posterPath: String,
)

@Entity
data class RemoteKeys(
    @PrimaryKey
    val movieId: Int,
    val prevKey: Int?,
    val nextKey: Int?,
)

fun List<DatabaseMovie>.asDomainModel(): List<Movie> {
    return map {
        Movie(
            id = it.id,
            title = it.title,
            posterPath = it.posterPath,
            overview = it.overview,
        )
    }
}

fun DatabaseMovie.asDomainModel(): Movie {
    return Movie(
        id = id,
        title = title,
        posterPath = posterPath,
        overview = overview,
    )
}
