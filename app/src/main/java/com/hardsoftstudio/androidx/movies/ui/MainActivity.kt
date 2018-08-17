package com.hardsoftstudio.androidx.movies.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.hardsoftstudio.androidx.movies.R
import com.hardsoftstudio.androidx.movies.domain.MainState
import com.hardsoftstudio.androidx.movies.ui.details.MovieDetailsFragment
import com.hardsoftstudio.androidx.movies.ui.list.MoviesListFragment
import org.koin.androidx.scope.ext.android.scopedWith
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.path.moduleName

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.getMainState().observe(this, Observer {
            when (it) {
                is MainState.MoviesList -> setFragment(MoviesListFragment.newInstance())
                is MainState.MovieDetails -> setFragment(MovieDetailsFragment.newInstance())
            }
        })

        scopedWith(this::class.moduleName)
    }

    override fun onBackPressed() {
        if (!viewModel.onBackPressed()) {
            super.onBackPressed()
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
    }
}
