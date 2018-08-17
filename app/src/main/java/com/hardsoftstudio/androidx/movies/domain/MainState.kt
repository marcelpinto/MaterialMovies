package com.hardsoftstudio.androidx.movies.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class MainState : Parcelable {

    @Parcelize
    data class MoviesList(val filter: MoviesRepository.Filter) : MainState()

    @Parcelize
    data class MovieDetails(val id: Int) : MainState()
}
