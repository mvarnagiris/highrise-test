package com.highrise.challenge.data

import com.highrise.challenge.core.MovieDetailSource
import com.highrise.challenge.core.MoviesSource
import life.shank.ShankModule
import life.shank.new

object DataModule : ShankModule {
    init {
        System.loadLibrary("native-lib")
    }

    val moviesSource = new<MoviesSource> { JniMoviesSource() }
    val movieDetailSource = new<MovieDetailSource> { JniMovieDetailSource() }
}