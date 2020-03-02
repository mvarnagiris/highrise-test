package com.highrise.challenge.ui.movies

import com.highrise.challenge.core.MoviesMvi
import com.highrise.challenge.data.DataModule.moviesSource
import life.shank.ShankModule
import life.shank.scoped

object MoviesModule : ShankModule {
    val moviesMvi = scoped { -> MoviesMvi(moviesSource()) }
}