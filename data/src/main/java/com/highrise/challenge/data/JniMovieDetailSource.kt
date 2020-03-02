package com.highrise.challenge.data

import com.highrise.challenge.core.MovieDetailSource
import com.highrise.challenge.data.models.JniMovieDetail
import com.highrise.challenge.models.MovieDetail
import com.highrise.challenge.models.MovieName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

internal class JniMovieDetailSource : MovieDetailSource {
    override suspend fun getMovieDetail(name: MovieName): MovieDetail? =
        withContext(Dispatchers.IO) {
            simulateSlowNetwork()
            getMovieDetailFromJNI(name.value)?.toMovieDetail()
        }

    private suspend fun simulateSlowNetwork() {
        delay(1000)
    }

    private external fun getMovieDetailFromJNI(name: String): JniMovieDetail?
}