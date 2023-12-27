package me.amitshekhar.mvvm.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import me.amitshekhar.mvvm.data.api.NetworkService
import me.amitshekhar.mvvm.data.model.MovieOneResponse
import me.amitshekhar.mvvm.data.model.MovieResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopHeadlineRepository @Inject constructor(private val networkService: NetworkService) {

    private var currentPage = 1


    fun getTopHeadlines(sort_by: String? = null): Flow<MovieResponse> {
        return flow {
            emit(networkService.getPaginatedMovieList(sort_by = sort_by))
        }.map {
            it
        }
    }

    fun getMovieDetailsHeadlines(id: Int): Flow<MovieOneResponse> {
        return flow {
            emit(networkService.getMovie(id = id))
        }.map {
            it
        }
    }


    // Add a function for fetching the next page
    suspend fun fetchNextPage(sort_by: String? = null): MovieResponse {
        currentPage++
        return networkService.getPaginatedMovieList(page = currentPage, sort_by = sort_by)
    }

}