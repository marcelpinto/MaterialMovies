package com.hardsoftstudio.androidx.movies.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hardsoftstudio.androidx.movies.domain.ErrorType
import com.hardsoftstudio.androidx.movies.domain.MainActions
import com.hardsoftstudio.androidx.movies.domain.MainState
import com.hardsoftstudio.androidx.movies.domain.MoviesRepository
import com.hardsoftstudio.androidx.movies.domain.data.Movie
import com.hardsoftstudio.androidx.movies.model.Resource
import com.hardsoftstudio.androidx.movies.model.WEB_URL
import com.hardsoftstudio.androidx.movies.utils.Event

class MoviesListViewModel(private val listActions: MainActions.List,
                          private val moviesRepository: MoviesRepository) : ViewModel() {

    private val sourceLiveData = MediatorLiveData<MoviesListViewData>()

    private var moviesSource: LiveData<Resource<List<Movie>, ErrorType>>? = null

    private val _eventLiveData = MutableLiveData<Event<MovieListEvent>>()

    private val filterList = listOf(
            MoviesRepository.Filter.Upcoming(),
            MoviesRepository.Filter.Year(2017),
            MoviesRepository.Filter.Year(2016),
            MoviesRepository.Filter.Year(2015),
            MoviesRepository.Filter.Date("2015-01-01")
    )

    val eventLiveData: LiveData<Event<MovieListEvent>> = _eventLiveData

    init {
        sourceLiveData.value = MoviesListViewData.Loading(false)
        sourceLiveData.addSource(listActions.getMainState()) {
            if (it is MainState.MoviesList) {
                requestMovies(it.filter)
            }
        }
    }

    fun getViewData(): LiveData<MoviesListViewData> = sourceLiveData

    fun onMovieSelected(movie: Movie) {
        listActions.onMovieSelected(movie.id)
    }

    fun onOpenFilter() {
        _eventLiveData.postValue(Event(MovieListEvent.OpenFilters(filterList)))
    }

    fun onFilterSelected(filter: MoviesRepository.Filter) {
        listActions.onFilterSelected(filter)
    }

    fun onRefreshMovies() {
        requestMovies(
                filter = getMoviesFilter() ?: MoviesRepository.Filter.Upcoming(),
                forceFetch = true
        )
    }

    fun onOpenWeb() {
        _eventLiveData.postValue(Event(MovieListEvent.OpenWeb(WEB_URL)))
    }

    private fun getMoviesFilter() = (listActions.getMainState().value as? MainState.MoviesList)?.filter

    private fun requestMovies(filter: MoviesRepository.Filter, forceFetch: Boolean = false) {
        moviesSource?.run {
            sourceLiveData.removeSource(this)
        }
        moviesSource = moviesRepository.getMovies(filter, forceFetch).apply {
            sourceLiveData.addSource(this) {
                updateViewData()
            }
        }
        updateViewData()
    }

    private fun updateViewData() {
        val data = moviesSource?.value
        sourceLiveData.value = when (data) {
            is Resource.Completed -> {
                val filter = getMoviesFilter() ?: MoviesRepository.Filter.Upcoming()
                MoviesListViewData.Page(data.data, filter)
            }
            is Resource.Failed -> MoviesListViewData.Error(data.error)
            null -> MoviesListViewData.Loading(false)
        }
    }
}

sealed class MoviesListViewData {

    data class Loading(val isForced: Boolean) : MoviesListViewData()

    data class Page(val page: List<Movie>, val filter: MoviesRepository.Filter) : MoviesListViewData()

    data class Error(val type: ErrorType) : MoviesListViewData()
}

sealed class MovieListEvent {
    data class OpenWeb(val url: String) : MovieListEvent()
    data class OpenFilters(val filters: List<MoviesRepository.Filter>) : MovieListEvent()
}
