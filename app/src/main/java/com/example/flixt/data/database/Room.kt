package com.example.flixt.data.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flixt.util.Constants.DATABASE_NAME


@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY releaseDate DESC")
    fun getMovies(): LiveData<List<DatabaseMovie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg movies: DatabaseMovie)
}

@Database(entities = [DatabaseMovie::class], version = 2)
abstract class MovieDatabase : RoomDatabase() {
    abstract val movieDao: MovieDao
}

private lateinit var INSTANCE: MovieDatabase

fun getDatabase(context: Context): MovieDatabase {
    if (!::INSTANCE.isInitialized) {
        synchronized(MovieDatabase::class.java) {
            INSTANCE = Room
                .databaseBuilder(context, MovieDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    return INSTANCE
}
