package com.highrise.challenge.ui.movie

import com.highrise.challenge.core.MovieDetailMvi
import com.highrise.challenge.data.DataModule.movieDetailSource
import com.highrise.challenge.models.MovieName
import life.shank.ShankModule
import life.shank.scoped

object MovieModule : ShankModule {
    val movieDetailMvi =
        scoped { movieName: MovieName -> MovieDetailMvi(movieName, movieDetailSource()) }
}