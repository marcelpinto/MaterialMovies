package com.hardsoftstudio.androidx.movies

import android.app.Application
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.startKoin

class MoviesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Picasso.setSingletonInstance(Picasso.Builder(this).build())
        startKoin(this, listOf(appModule, contextModule))
    }
}
