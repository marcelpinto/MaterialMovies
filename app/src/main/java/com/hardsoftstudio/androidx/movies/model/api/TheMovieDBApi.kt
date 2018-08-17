package com.hardsoftstudio.androidx.movies.model.api

import androidx.lifecycle.LiveData
import com.hardsoftstudio.androidx.movies.model.API_VERSION
import retrofit2.http.GET
import retrofit2.http.Query

interface TheMovieDBApi {

    //@GET("/$API_VERSION/discover/movie?sort_by=primary_release_date.desc")
    @GET("/$API_VERSION/movie/now_playing")
    fun getNewMovies(): LiveData<ApiResponse<DiscoverResponse>>

    @GET("/$API_VERSION/discover/movie")
    fun getMoviesByYear(@Query("primary_release_year") year: Int)
            : LiveData<ApiResponse<DiscoverResponse>>
}
