package com.example.flixt.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.flixt.network.NetworkMovie
import com.example.flixt.network.TmdbApiService
import retrofit2.HttpException
import java.io.IOException
import kotlin.math.max

// The initial key used for loading
private const val STARTING_PAGE = 1

/**
 * A [PagingSource] that loads movies for paging. The [Int] key is the paging key or query
 * that is used to fetch more data, and the [NetworkMovie] specifies that the [PagingSource]
 * fetches a [NetworkMovie] [List].
 */
class MoviePagingSource(private val service: TmdbApiService) :
    PagingSource<Int, NetworkMovie>() {

    // The refresh key is used for qthe initial load for the next paging source, after invalidation
    override fun getRefreshKey(state: PagingState<Int, NetworkMovie>): Int {
        // In our case, we do not care about preserving the scroll position when refreshed
        // hence we start at the beginning
        return STARTING_PAGE
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NetworkMovie> {
        // If params.key is null, it is the first load, so we start with STARTING_PAGE
        val startKey = params.key ?: STARTING_PAGE

        return try {
            val response = service.getMovies(startKey)
            val movies = response.movies

            LoadResult.Page(
                data = movies,
                prevKey = if (startKey == STARTING_PAGE) null else ensureValidKey(startKey - 1),
                nextKey = if (response.totalPages <= startKey) null else startKey + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    /**
     * Makes sure the paging key is never less than [STARTING_PAGE]
     */
    private fun ensureValidKey(key: Int) = max(STARTING_PAGE, key)
}
