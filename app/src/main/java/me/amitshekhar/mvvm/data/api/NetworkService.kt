package me.amitshekhar.mvvm.data.api

import me.amitshekhar.mvvm.data.model.MovieOneResponse
import me.amitshekhar.mvvm.data.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface NetworkService {

    @GET("list_movies.json")
    suspend fun getPaginatedMovieList(
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int = 1,
        @Query("query_term") queryTerm: String? = null,
        @Query("sort_by") sort_by: String? = null
    ): MovieResponse


    @GET("movie_details.json")
    suspend fun getMovie(
        @Query("movie_id") id: Int
    ): MovieOneResponse
}