package com.hardsoftstudio.androidx.movies.ui.details

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
import org.junit.Before
import org.junit.Test

class MovieDetailsViewModelTest : BaseUnitTest() {

    private lateinit var tested: MovieDetailsViewModel

    private val viewDataObserver = mockk<Observer<MovieDetailsViewData>>(relaxed = true)
    private val detailsActions = mockk<MainActions.Details>(relaxed = true)
    private val moviesRepository = mockk<MoviesRepository>(relaxed = true)

    private val stateLiveData = MutableLiveData<MainState>()
    private val repoLiveData = MutableLiveData<Resource<Movie, ErrorType>>()

    @Before
    fun setUp() {
        every { detailsActions.getMainState() } returns stateLiveData
        every { moviesRepository.getMovie(movie.id) } returns repoLiveData

        tested = MovieDetailsViewModel(detailsActions, moviesRepository).apply {
            getViewData().observeForever(viewDataObserver)
        }
    }

    @Test
    fun `Given initial state When observing view data Then state is loading`() {
        assertThat(tested.getViewData().value).isEqualTo(MovieDetailsViewData.Loading)
    }

    @Test
    fun `Given Movie id When repo loading Then view data is loading`() {
        stateLiveData.value = MainState.MovieDetails(movie.id)

        assertThat(tested.getViewData().value).isEqualTo(MovieDetailsViewData.Loading)
    }

    @Test
    fun `Given Movie id When query success Then view data is movie`() {
        repoLiveData.value = Resource.Completed(movie)

        stateLiveData.value = MainState.MovieDetails(movie.id)

        val expectedMovie = MovieDetailsViewData.Details(movie)
        assertThat(tested.getViewData().value).isEqualTo(expectedMovie)
    }

    @Test
    fun `Given movie id When query fails Then view data is error`() {
        val error = ErrorType.NETWORK
        repoLiveData.value = Resource.Failed(error)

        stateLiveData.value = MainState.MovieDetails(movie.id)

        val expectedError = MovieDetailsViewData.Error(error)
        assertThat(tested.getViewData().value).isEqualTo(expectedError)
    }
}
