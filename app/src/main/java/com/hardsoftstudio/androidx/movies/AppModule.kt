package com.hardsoftstudio.androidx.movies

import com.hardsoftstudio.androidx.movies.domain.MainActions
import com.hardsoftstudio.androidx.movies.domain.MainInteractor
import com.hardsoftstudio.androidx.movies.domain.MoviesRepository
import com.hardsoftstudio.androidx.movies.model.*
import com.hardsoftstudio.androidx.movies.model.api.LiveDataCallAdapterFactory
import com.hardsoftstudio.androidx.movies.model.api.NetworkFactory
import com.hardsoftstudio.androidx.movies.model.api.TheMovieDBApi
import com.hardsoftstudio.androidx.movies.ui.MainActivity
import com.hardsoftstudio.androidx.movies.ui.MainViewModel
import com.hardsoftstudio.androidx.movies.ui.details.MovieDetailsViewModel
import com.hardsoftstudio.androidx.movies.ui.list.MoviesListViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import org.koin.dsl.path.moduleName


val appModule: Module = module {

    single { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }

    single { LiveDataCallAdapterFactory() }

    single { NetworkFactory(get(), API_KEY, get()) }

    single { get<NetworkFactory>().createApi(TheMovieDBApi::class.java, API_BASE_URL) }

    single { AppMoviesRepository(get(), get()) as MoviesRepository }

    module(MainActivity::class.moduleName) {
        single { MainInteractor() } bind MainActions::class
        single { get<MainInteractor>() as MainActions.List }
        single { get<MainInteractor>() as MainActions.Details }
        viewModel { MainViewModel(get()) }
        viewModel { MoviesListViewModel(get(), get()) }
        viewModel { MovieDetailsViewModel(get(), get()) }
    }
}

val contextModule: Module = module {

    single { AndroidPlatform(androidContext()) as Platform }
}
