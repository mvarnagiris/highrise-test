package com.highrise.challenge.data

import com.highrise.challenge.core.MoviesSource
import com.highrise.challenge.data.models.JniMovie
import com.highrise.challenge.models.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

internal class JniMoviesSource : MoviesSource {
    override suspend fun getMovies(): List<Movie> = withContext(Dispatchers.IO) {
        simulateSlowNetwork()
        getMoviesFromJNI().map { it.toMovie() }
    }

    private suspend fun simulateSlowNetwork() {
        delay(1000)
    }

    private external fun getMoviesFromJNI(): Array<JniMovie>
}
