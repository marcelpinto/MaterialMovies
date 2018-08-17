package com.hardsoftstudio.androidx.movies.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.hardsoftstudio.androidx.movies.R
import com.hardsoftstudio.androidx.movies.domain.MoviesRepository
import kotlinx.android.synthetic.main.fragment_movies_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MoviesListFragment : Fragment(), FilterListDialogFragment.Listener {

    companion object {
        fun newInstance() = MoviesListFragment()
    }

    private val viewModel: MoviesListViewModel by viewModel()

    private lateinit var adapter: MoviesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_movies_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moviesBottomBar.replaceMenu(R.menu.movies_menu)
        moviesBottomBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.reload -> viewModel.onRefreshMovies()
                R.id.openWeb -> viewModel.onOpenWeb()
            }
            true
        }
        filterFab.setOnClickListener {
            viewModel.onOpenFilter()
        }
        adapter = MoviesAdapter { selectedMovie ->
            viewModel.onMovieSelected(selectedMovie)
        }
        moviesList.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getViewData().observe(this, Observer {
            when (it) {
                is MoviesListViewData.Loading -> showLoading()
                is MoviesListViewData.Page -> updatePage(it)
                is MoviesListViewData.Error -> showError()
            }
        })

        viewModel.eventLiveData.observe(this, Observer {
            val event = it.getContentIfNotHandled()
            when (event) {
                is MovieListEvent.OpenWeb -> startActivity(Intent.parseUri(event.url, 0))
                is MovieListEvent.OpenFilters -> {
                    FilterListDialogFragment.newInstance(event.filters).show(childFragmentManager, "filters")
                }
            }
        })
    }

    override fun onFilterSelected(filter: MoviesRepository.Filter) {
        viewModel.onFilterSelected(filter)
    }

    private fun showError() {
        moviesList.isGone = true
        filterFab.hide()
        moviesLoading.hide()
        Snackbar.make(view!!, getString(R.string.list_error), Snackbar.LENGTH_LONG).show()
    }

    private fun showLoading() {
        moviesList.isGone = true
        filterFab.hide()
        moviesLoading.show()
    }

    private fun updatePage(page: MoviesListViewData.Page) {
        moviesList.isVisible = true
        filterFab.show()
        moviesLoading.hide()

        adapter.submitList(page.page)
    }
}
