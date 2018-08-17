package com.hardsoftstudio.androidx.movies.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hardsoftstudio.androidx.movies.R
import com.hardsoftstudio.androidx.movies.domain.data.Movie
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_movie.*

class MoviesAdapter(private val selectCallback: (Movie) -> Unit)
    : ListAdapter<Movie, MovieViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val contentView = inflater.inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(contentView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
        holder.containerView.setOnClickListener {
            selectCallback(movie)
        }
    }
}

class MovieViewHolder(override val containerView: View)
    : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(movie: Movie) {
        movieTitle.text = movie.title
        movieRating.text = movie.rating.toString()
        Picasso.get().load(movie.imageUrl)
                .noPlaceholder()
                .fit()
                .into(movieImage)
    }
}
