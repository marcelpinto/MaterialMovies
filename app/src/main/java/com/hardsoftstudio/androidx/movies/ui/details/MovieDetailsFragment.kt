package com.hardsoftstudio.androidx.movies.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.hardsoftstudio.androidx.movies.R
import com.hardsoftstudio.androidx.movies.domain.ErrorType
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_movie_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = MovieDetailsFragment()
    }

    private val viewModel: MovieDetailsViewModel by viewModel()

    private val loadingColor: Int by lazy {
        ResourcesCompat.getColor(resources, R.color.colorTextLoading, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getViewData().observe(this, Observer {
            when (it) {
                MovieDetailsViewData.Loading -> showLoading()
                is MovieDetailsViewData.Details -> showDetails(it)
                is MovieDetailsViewData.Error -> showError(it.type)
            }
        })
    }

    private fun showLoading() {
        detailsTitle.setTextState()
        detailsReleaseDate.setTextState()
        detailsRating.setTextState()
        detailsDescription.setTextState()
        detailsCast.setTextState()
    }

    private fun showDetails(details: MovieDetailsViewData.Details) {
        details.movie.run {
            detailsTitle.setTextState(title)
            detailsReleaseDate.setTextState(getString(R.string.details_release_date, releaseDate))
            detailsRating.setTextState(getString(R.string.details_rating, rating.toFloat()))
            detailsDescription.setTextState(description)
            detailsCast.setTextState("TODO")
            Picasso.get().load(imageUrl)
                    .noPlaceholder()
                    .fit()
                    .into(detailsImage)
        }
    }

    private fun showError(type: ErrorType) {
        Snackbar.make(view!!, type.name, Snackbar.LENGTH_LONG).show()
    }

    private fun TextView.setTextState(text: String? = null) {
        if (text == null) {
            setBackgroundColor(loadingColor)
        } else {
            background = null
        }
        this.text = text
    }
}
