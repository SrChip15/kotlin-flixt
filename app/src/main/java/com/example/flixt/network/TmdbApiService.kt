package com.example.flixt.network

import com.example.flixt.BuildConfig
import com.example.flixt.util.Constants.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(Logger.getLogger())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()


interface TmdbApiService {
    @GET("discover/movie")
    suspend fun getMovies(
        @Query("page") page: Int
    ): DiscoverResponse
}


object TmdbApi {
    val retrofitService: TmdbApiService by lazy {
        retrofit.create(TmdbApiService::class.java)
    }
}

object Logger {
    fun getLogger(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val originalUrl = original.url

            // add authorization as query parameter
            val url = originalUrl.newBuilder()
                .addQueryParameter("api_key", BuildConfig.API_KEY)
                .build()

            val requestBuilder = original.newBuilder().url(url)

            val request = requestBuilder.build()

            chain.proceed(request)
        }

        return httpClient.build()
    }
}
