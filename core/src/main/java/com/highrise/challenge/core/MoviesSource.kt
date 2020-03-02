package com.highrise.challenge.core

import com.highrise.challenge.models.Movie

interface MoviesSource {
    suspend fun getMovies(): List<Movie>
}