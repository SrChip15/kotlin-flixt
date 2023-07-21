package com.example.flixt.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.flixt.domain.Movie

@Entity(tableName = "movies")
data class DatabaseMovie constructor(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String,
    val backdropPath: String?,
    val releaseDate: String,
    val video: Boolean,
    val popularity: Double,
    val voteCount: Int,
    val voteAverage: Double,
    val originalTitle: String,
    val posterPath: String,
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
