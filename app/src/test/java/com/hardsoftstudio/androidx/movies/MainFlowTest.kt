package com.hardsoftstudio.androidx.movies

import com.google.common.truth.Truth.assertThat
import com.hardsoftstudio.androidx.movies.domain.ErrorType
import com.hardsoftstudio.androidx.movies.domain.MainState
import com.hardsoftstudio.androidx.movies.domain.MoviesRepository
import com.hardsoftstudio.androidx.movies.model.Platform
import com.hardsoftstudio.androidx.movies.model.api.LiveDataCallAdapterFactory
import com.hardsoftstudio.androidx.movies.model.api.NetworkFactory
import com.hardsoftstudio.androidx.movies.model.api.TheMovieDBApi
import com.hardsoftstudio.androidx.movies.test.InstantLiveDataCallAdapterFactory
import com.hardsoftstudio.androidx.movies.test.upcomingResponse
import com.hardsoftstudio.androidx.movies.test.year2017Response
import com.hardsoftstudio.androidx.movies.ui.MainViewModel
import com.hardsoftstudio.androidx.movies.ui.details.MovieDetailsViewData
import com.hardsoftstudio.androidx.movies.ui.details.MovieDetailsViewModel
import com.hardsoftstudio.androidx.movies.ui.list.MoviesListViewData
import com.hardsoftstudio.androidx.movies.ui.list.MoviesListViewModel
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.declare

/**
 * Test the main flow of the application without the views.
 *
 * This test behaves like the normal application but without any view creation, it observes the liveData
 * as the fragment or activity would do and uses the MockWebServer to intercept request and return mocked data.
 *
 * Note that we re-declare some android specific dependencies to make the full code execute synchronously to ensure
 * test stability.
 */
class MainFlowTest : BaseUnitTest(), KoinTest {

    private val mockWebServer = MockWebServer()
    private val httpUrl = mockWebServer.url("/")

    private val mainViewModel by inject<MainViewModel>()
    private val moviesListViewModel by inject<MoviesListViewModel>()
    private val movieDetailsViewModel by inject<MovieDetailsViewModel>()

    private val testContextModule: Module = module {

        single<Platform> {
            object : Platform {
                override fun postOnMain(delayMs: Long, tag: Any?, action: () -> Unit) {
                    action()
                }

                override fun removeFromMain(tag: Any?) {
                }

                override fun postOnDisk(action: () -> Unit) {
                    action()
                }
            }
        }
    }

    private val yearFilter = MoviesRepository.Filter.Year(2017)

    private val upcomingMoviesResponse = MockResponse().apply {
        setResponseCode(200)
        setBody(upcomingResponse)
    }
    private val yearMoviesResponse = MockResponse().apply {
        setResponseCode(200)
        setBody(year2017Response)
    }

    @Before
    fun setUp() {
        startKoin(listOf(appModule, testContextModule), createOnStart = false)
        declare {

            single(override = true) { InstantLiveDataCallAdapterFactory() as LiveDataCallAdapterFactory }

            single(override = true) {
                get<NetworkFactory>().createApi(TheMovieDBApi::class.java, httpUrl.toString())
            }
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `Start flow is loading`() {
        mainViewModel.getMainState().observeForever { newState ->
            val viewData = moviesListViewModel.getViewData().value
            assertThat(newState).isInstanceOf(MainState.MoviesList::class.java)
            assertThat(viewData).isEqualTo(MoviesListViewData.Loading(false))
        }
    }

    @Test
    fun `Start flow when page loaded`() {
        mockWebServer.enqueue(upcomingMoviesResponse)

        observeFlow()
        mockWebServer.takeRequest()

        val page = moviesListViewModel.getViewData().value as MoviesListViewData.Page
        assertThat(page.filter).isInstanceOf(MoviesRepository.Filter.Upcoming::class.java)
        assertThat(page.page.size).isEqualTo(2)
        assertThat(page.page[0].id).isEqualTo(297761)
        assertThat(page.page[1].id).isEqualTo(324668)
    }

    @Test
    fun `Start flow when page failed`() {
        val failedResponse = MockResponse().setResponseCode(400)
        mockWebServer.enqueue(failedResponse)

        observeFlow()
        mockWebServer.takeRequest()

        val page = moviesListViewModel.getViewData().value as MoviesListViewData.Error
        assertThat(page.type).isEqualTo(ErrorType.INTERNAL)
    }

    @Test
    fun `Start flow and change filter`() {
        mockWebServer.enqueue(upcomingMoviesResponse)
        mockWebServer.enqueue(yearMoviesResponse)

        observeFlow()
        mockWebServer.takeRequest()
        moviesListViewModel.onFilterSelected(yearFilter)
        mockWebServer.takeRequest()

        val page = moviesListViewModel.getViewData().value as MoviesListViewData.Page
        assertThat(page.filter).isEqualTo(yearFilter)
        assertThat(page.page.size).isEqualTo(2)
        assertThat(page.page[0].id).isEqualTo(164558)
        assertThat(page.page[1].id).isEqualTo(654)
    }

    @Test
    fun `Start flow and select movie`() {
        mockWebServer.enqueue(upcomingMoviesResponse)

        observeFlow()
        mockWebServer.takeRequest()
        moviesListViewModel.onMovieSelected(movie.copy(id = 297761))

        val page = movieDetailsViewModel.getViewData().value as MovieDetailsViewData.Details
        assertThat(page.movie.id).isEqualTo(297761)
    }

    private fun observeFlow() {
        mainViewModel.getMainState().observeForever { newState ->
            when (newState) {
                is MainState.MoviesList -> {
                    moviesListViewModel.getViewData().observeForever {
                    }
                }
                is MainState.MovieDetails -> {
                    movieDetailsViewModel.getViewData().observeForever {
                    }
                }
            }
        }
    }
}
