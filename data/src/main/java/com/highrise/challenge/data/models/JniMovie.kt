package com.highrise.challenge.data.models

import com.highrise.challenge.models.Movie
import com.highrise.challenge.models.MovieName
import com.highrise.challenge.models.Timestamp

internal class JniMovie(
    private val name: String,
    private val lastUpdated: Int
) {
    fun toMovie() = Movie(MovieName(name), Timestamp(lastUpdated.toLong()))
}