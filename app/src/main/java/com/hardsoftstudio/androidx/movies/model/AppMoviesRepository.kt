package com.hardsoftstudio.androidx.movies.model

import androidx.collection.LruCache
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hardsoftstudio.androidx.movies.domain.ErrorType
import com.hardsoftstudio.androidx.movies.domain.MoviesRepository
import com.hardsoftstudio.androidx.movies.domain.data.Movie
import com.hardsoftstudio.androidx.movies.model.api.ApiResponse
import com.hardsoftstudio.androidx.movies.model.api.DiscoverResponse
import com.hardsoftstudio.androidx.movies.model.api.TheMovieDBApi

class AppMoviesRepository(private val platform: Platform,
                          private val moviesApi: TheMovieDBApi) : MoviesRepository {

    private val movieCache = LruCache<Int, Movie>(1000)
    private val queryCache = LruCache<MoviesRepository.Filter, List<Movie>>(10)

    private val onGoingRequest = HashMap<MoviesRepository.Filter, GetMovies>()

    override fun getMovies(filter: MoviesRepository.Filter,
                           forceFetch: Boolean
    ): LiveData<Resource<List<Movie>, ErrorType>> {
        val request = onGoingRequest[filter] ?: GetMovies(filter, forceFetch)
        onGoingRequest[filter] = request
        return request.get()
    }

    override fun getMovie(id: Int): LiveData<Resource<Movie, ErrorType>> {
        return MutableLiveData<Resource<Movie, ErrorType>>().apply {
            val movie = movieCache.get(id)
            value = if (movie != null) {
                Resource.Completed(movie)
            } else {
                Resource.Failed(ErrorType.INTERNAL)
            }
        }
    }

    private inner class GetMovies(private val filter: MoviesRepository.Filter,
                                  private val forceFetch: Boolean)
        : NetworkBoundResource<List<Movie>, DiscoverResponse, ErrorType>(platform) {

        override fun saveResult(result: DiscoverResponse) {
            val convertedResult = result.results.map {
                val movie = it.run {
                    Movie(
                            id,
                            title,
                            overview,
                            voteAverage,
                            releaseDate,
                            API_IMAGE_URL + posterPath,
                            emptyList()
                    )
                }
                movieCache.put(it.id, movie)
                movie
            }
            queryCache.put(filter, convertedResult)
        }

        override fun shouldFetch(data: List<Movie>?): Boolean = data == null || forceFetch

        override fun fetchFromPersistence(): LiveData<List<Movie>> {
            return MutableLiveData<List<Movie>>().apply {
                value = queryCache.get(filter)
            }
        }

        override fun createCall(): LiveData<ApiResponse<DiscoverResponse>> {
            return when (filter) {
                is MoviesRepository.Filter.Upcoming -> moviesApi.getNewMovies()
                is MoviesRepository.Filter.Year -> moviesApi.getMoviesByYear(filter.year)
                is MoviesRepository.Filter.Date -> {
                    // TODO implement API by date
                    MutableLiveData<ApiResponse<DiscoverResponse>>().apply {
                        postValue(ApiResponse.Failed(UnsupportedOperationException()))
                    }
                }
            }
        }

        override fun processError(response: ApiResponse.Completed.Error<DiscoverResponse>): ErrorType {
            return ErrorType.INTERNAL
        }

        override fun processFailure(throwable: Throwable): ErrorType {
            return ErrorType.NETWORK
        }

        override fun onDisposed() {
            onGoingRequest.remove(filter)
            super.onDisposed()
        }

        override fun processSuccess(response: ApiResponse.Completed.Success<DiscoverResponse>): DiscoverResponse? {
            onGoingRequest.remove(filter)
            return super.processSuccess(response)
        }
    }
}
