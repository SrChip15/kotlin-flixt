package com.example.flixt.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.flixt.BuildConfig
import com.example.flixt.network.NetworkMovie
import com.example.flixt.network.TmdbApiService
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE = 1

class MoviePagingSource(private val service: TmdbApiService) :
    PagingSource<Int, NetworkMovie>() {
    override fun getRefreshKey(state: PagingState<Int, NetworkMovie>): Int {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NetworkMovie> {
        val position = params.key ?: STARTING_PAGE

        return try {
            val response = service.getMovies(BuildConfig.API_KEY, position)
            val movies = response.movies

            LoadResult.Page(
                data = movies,
                prevKey = if (position == STARTING_PAGE) null else position - 1,
                nextKey = if (response.totalPages <= position) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}
