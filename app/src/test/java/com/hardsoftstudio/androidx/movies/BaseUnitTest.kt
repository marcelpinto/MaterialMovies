package com.hardsoftstudio.androidx.movies

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hardsoftstudio.androidx.movies.domain.data.Movie
import org.junit.Rule

abstract class BaseUnitTest {

    protected val movie = Movie(
            id = 1,
            title = "title",
            description = "description",
            rating = 5.0,
            releaseDate = "date",
            imageUrl = "image url",
            actorsName = emptyList()
    )

    @Rule
    @JvmField
    val liveDataRule = InstantTaskExecutorRule()
}
