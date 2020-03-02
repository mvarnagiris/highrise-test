package com.highrise.challenge.core

import com.highrise.challenge.models.MovieDetail
import com.highrise.challenge.models.MovieName

interface MovieDetailSource {
    suspend fun getMovieDetail(name: MovieName): MovieDetail?
}