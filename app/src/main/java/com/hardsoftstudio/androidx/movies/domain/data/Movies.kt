package com.hardsoftstudio.androidx.movies.domain.data

data class Movie(val id: Int,
                 val title: String,
                 val description: String,
                 val rating: Double,
                 val releaseDate: String,
                 val imageUrl: String?,
                 val actorsName: List<String>
)
