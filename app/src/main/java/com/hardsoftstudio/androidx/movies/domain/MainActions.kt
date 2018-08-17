package com.hardsoftstudio.androidx.movies.domain

import androidx.lifecycle.LiveData

interface MainActions {

    fun getMainState(): LiveData<MainState>

    fun goBack(): Boolean

    interface List : MainActions {

        fun onFilterSelected(filter: MoviesRepository.Filter)

        fun onMovieSelected(id: Int)
    }

    interface Details : MainActions
}
