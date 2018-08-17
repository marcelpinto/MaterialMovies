package com.hardsoftstudio.androidx.movies.domain

import android.os.Parcelable
import androidx.lifecycle.LiveData
import com.hardsoftstudio.androidx.movies.domain.data.Movie
import com.hardsoftstudio.androidx.movies.model.Resource
import kotlinx.android.parcel.Parcelize

interface MoviesRepository {

    fun getMovies(filter: Filter, forceFetch: Boolean = false): LiveData<Resource<List<Movie>, ErrorType>>

    fun getMovie(id: Int): LiveData<Resource<Movie, ErrorType>>

    sealed class Filter : Parcelable {

        @Parcelize
        class Upcoming : Filter()

        @Parcelize
        data class Year(val year: Int) : Filter()

        @Parcelize
        data class Date(val olderThanDate: String) : Filter()
    }
}
