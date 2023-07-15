package com.example.flixt.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.flixt.BuildConfig
import com.example.flixt.network.Movie
import com.example.flixt.network.TmdbApiService
import retrofit2.HttpException
import java.io.IOException

class MovieListPagingSource(private val apiService: TmdbApiService) : PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getMovies(BuildConfig.API_KEY, page)
            // val data = response.results
            // val responseData = mutableListOf<Movie>()
            // responseData.addAll(data)

            LoadResult.Page(
                data = response.results,
                prevKey = page - 1,
                nextKey = response.page + 1
            )
        } catch (ioException: IOException) {
            LoadResult.Error(ioException)
        } catch (httpException: HttpException) {
            LoadResult.Error(httpException)
        }
    }
}