package com.hardsoftstudio.androidx.movies.domain

import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.hardsoftstudio.androidx.movies.BaseUnitTest
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class MainInteractorTest : BaseUnitTest() {

    private lateinit var tested: MainInteractor

    @Before
    fun setUp() {
        tested = MainInteractor()
    }

    @Test
    fun `When initial state Then state is MovieList with upcoming filter`() {
        val state = tested.getMainState().value
        assertThat(state).isInstanceOf(MainState.MoviesList::class.java)
        val filter = (state as MainState.MoviesList).filter
        assertThat(filter).isInstanceOf(MoviesRepository.Filter.Upcoming::class.java)
    }

    @Test
    fun `Given one state in back stack When go back Then returns false`() {
        val goBack = tested.goBack()

        assertThat(goBack).isFalse()
    }

    @Test
    fun `Given one state in back stack When go back Then does not notify state change`() {
        val observer = mockk<Observer<MainState>>(relaxed = true)
        tested.getMainState().observeForever(observer)

        tested.goBack()

        verify(exactly = 1) { observer.onChanged(any()) }
    }

    @Test
    fun `Given two states in back stack When go back Then does returns true`() {
        // Add move details state
        tested.onMovieSelected(1)

        val goBack = tested.goBack()

        assertThat(goBack).isTrue()
    }

    @Test
    fun `Given two states in back stack When go back Then notify state change`() {
        val observer = mockk<Observer<MainState>>(relaxed = true)
        tested.getMainState().observeForever(observer)

        tested.goBack()

        verify { observer.onChanged(assert { it is MainState.MoviesList }) }
    }

    @Test
    fun `Given in MovieList state When filter selected Then update state`() {
        val filter = MoviesRepository.Filter.Year(2017)
        val observer = mockk<Observer<MainState>>(relaxed = true)
        tested.getMainState().observeForever(observer)

        tested.onFilterSelected(filter)

        verify {
            observer.onChanged(
                    match {
                        (it as? MainState.MoviesList)?.filter == filter
                    }
            )
        }
    }

    @Test
    fun `Given in MovieList state When movie details selected Then update state`() {
        val observer = mockk<Observer<MainState>>(relaxed = true)
        tested.getMainState().observeForever(observer)

        tested.onMovieSelected(movie.id)

        verify {
            observer.onChanged(
                    match {
                        (it as? MainState.MovieDetails)?.id == movie.id
                    }
            )
        }
    }
}
