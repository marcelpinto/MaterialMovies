package com.hardsoftstudio.androidx.movies.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.hardsoftstudio.androidx.movies.domain.ErrorType
import com.hardsoftstudio.androidx.movies.domain.MainActions
import com.hardsoftstudio.androidx.movies.domain.MainState
import com.hardsoftstudio.androidx.movies.domain.MoviesRepository
import com.hardsoftstudio.androidx.movies.domain.data.Movie
import com.hardsoftstudio.androidx.movies.model.Resource

class MovieDetailsViewModel(detailsActions: MainActions.Details,
                            private val moviesRepository: MoviesRepository) : ViewModel() {

    private val sourceLiveData = MediatorLiveData<MovieDetailsViewData>()

    private var movieSource: LiveData<Resource<Movie, ErrorType>>? = null

    init {
        sourceLiveData.value = MovieDetailsViewData.Loading
        sourceLiveData.addSource(detailsActions.getMainState()) {
            if (it is MainState.MovieDetails) {
                movieSource?.run {
                    sourceLiveData.removeSource(this)
                }
                movieSource = moviesRepository.getMovie(it.id).apply {
                    sourceLiveData.addSource(this) {
                        updateViewData()
                    }
                }
                updateViewData()
            }
        }
    }

    fun getViewData(): LiveData<MovieDetailsViewData> = sourceLiveData

    private fun updateViewData() {
        val data = movieSource?.value
        sourceLiveData.value = when (data) {
            is Resource.Completed -> {
                data.data.run {
                    MovieDetailsViewData.Details(this)
                }
            }
            is Resource.Failed -> MovieDetailsViewData.Error(data.error)
            null -> MovieDetailsViewData.Loading
        }
    }
}

sealed class MovieDetailsViewData {

    object Loading : MovieDetailsViewData()

    data class Details(val movie: Movie) : MovieDetailsViewData()

    data class Error(val type: ErrorType) : MovieDetailsViewData()
}
