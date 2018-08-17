package com.hardsoftstudio.androidx.movies.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.hardsoftstudio.androidx.movies.BaseUnitTest
import com.hardsoftstudio.androidx.movies.domain.ErrorType
import com.hardsoftstudio.androidx.movies.domain.MainActions
import com.hardsoftstudio.androidx.movies.domain.MainState
import com.hardsoftstudio.androidx.movies.domain.MoviesRepository
import com.hardsoftstudio.androidx.movies.domain.data.Movie
import com.hardsoftstudio.androidx.movies.model.Resource
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class MoviesListViewModelTest : BaseUnitTest() {

    private lateinit var tested: MoviesListViewModel

    private val moviesList = listOf(
            movie,
            movie.copy(id = 2),
            movie.copy(id = 3)
    )

    private val upcomingFilter = MoviesRepository.Filter.Upcoming()
    private val yearFilter = MoviesRepository.Filter.Year(2017)

    private val viewDataObserver = mockk<Observer<MoviesListViewData>>(relaxed = true)
    private val listActions = mockk<MainActions.List>(relaxed = true)
    private val moviesRepository = mockk<MoviesRepository>(relaxed = true)

    private val stateLiveData = MutableLiveData<MainState>()
    private val repoLiveData = MutableLiveData<Resource<List<Movie>, ErrorType>>()

    @Before
    fun setUp() {
        every { listActions.getMainState() } returns stateLiveData
        every { moviesRepository.getMovies(any()) } returns repoLiveData

        tested = MoviesListViewModel(listActions, moviesRepository).apply {
            getViewData().observeForever(viewDataObserver)
        }
    }

    @Test
    fun `Given initial state When observing view data Then state is loading`() {
        assertThat(tested.getViewData().value).isEqualTo(MoviesListViewData.Loading(false))
    }

    @Test
    fun `Given upcoming filter When repo loading Then view data is loading`() {
        stateLiveData.value = MainState.MoviesList(MoviesRepository.Filter.Upcoming())

        assertThat(tested.getViewData().value).isEqualTo(MoviesListViewData.Loading(false))
    }

    @Test
    fun `Given upcoming filter When query success Then view data is page with movies`() {
        repoLiveData.value = Resource.Completed(moviesList)

        stateLiveData.value = MainState.MoviesList(upcomingFilter)

        val expectedPage = MoviesListViewData.Page(moviesList, upcomingFilter)
        assertThat(tested.getViewData().value).isEqualTo(expectedPage)
    }

    @Test
    fun `Given upcoming filter When query fails Then view data is error`() {
        val error = ErrorType.NETWORK
        repoLiveData.value = Resource.Failed(error)

        stateLiveData.value = MainState.MoviesList(upcomingFilter)

        val expectedError = MoviesListViewData.Error(error)
        assertThat(tested.getViewData().value).isEqualTo(expectedError)
    }

    @Test
    fun `Given upcoming filter When change to year filter Then new filter data page is shown`() {
        repoLiveData.value = Resource.Completed(moviesList)
        stateLiveData.value = MainState.MoviesList(upcomingFilter)

        val yearMovies = listOf(movie.copy(id = 100))
        repoLiveData.value = Resource.Completed(yearMovies)
        stateLiveData.value = MainState.MoviesList(yearFilter)

        val expectedPage = MoviesListViewData.Page(yearMovies, yearFilter)
        assertThat(tested.getViewData().value).isEqualTo(expectedPage)
    }

    @Test
    fun `When filter selected Then notify actions`() {
        tested.onFilterSelected(yearFilter)

        verify { listActions.onFilterSelected(yearFilter) }
    }

    @Test
    fun `When movie selected Then notify actions`() {
        tested.onMovieSelected(movie)

        verify { listActions.onMovieSelected(movie.id) }
    }

    @Test
    fun `Given upcoming filter When refresh movies Then page has updated movies`() {
        val refreshedMovies = moviesList.plus(movie.copy(id = moviesList.size))
        val refreshLiveData = MutableLiveData<Resource<List<Movie>, ErrorType>>().apply {
            value = Resource.Completed(refreshedMovies)
        }
        every {
            moviesRepository.getMovies(filter = any(), forceFetch = true)
        } returns refreshLiveData
        repoLiveData.value = Resource.Completed(moviesList)
        stateLiveData.value = MainState.MoviesList(upcomingFilter)

        tested.onRefreshMovies()

        val expectedPage = MoviesListViewData.Page(refreshedMovies, upcomingFilter)
        assertThat(tested.getViewData().value).isEqualTo(expectedPage)
        verify { moviesRepository.getMovies(upcomingFilter, true) }
    }

    @Test
    fun `When open filter Then event live data is OpenFilter`() {
        tested.onOpenFilter()

        val event = tested.eventLiveData.value?.getContentIfNotHandled()
        assertThat(event).isInstanceOf(MovieListEvent.OpenFilters::class.java)
    }

    @Test
    fun `When open web Then event live data is OpenWeb`() {
        tested.onOpenWeb()

        val event = tested.eventLiveData.value?.getContentIfNotHandled()
        assertThat(event).isInstanceOf(MovieListEvent.OpenWeb::class.java)
    }
}
