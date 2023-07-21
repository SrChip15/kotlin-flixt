package com.example.flixt.network

import com.example.flixt.network.TmdbApiService.Companion.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query



private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


interface TmdbApiService {
    //TODO: Add authorization to client
    @GET("discover/movie")
    suspend fun getMovies(@Query("api_key") apiKey: String, @Query("page") page: Int): DiscoverResponse

    // TODO: Move to constants file
    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val POSTER_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/"
        const val POSTER_IMAGE_SIZE = "w780"
    }
}


object TmdbApi {
    val retrofitService: TmdbApiService by lazy {
        retrofit.create(TmdbApiService::class.java)
    }
}