package com.highrise.challenge.data.models

import com.highrise.challenge.models.Description
import com.highrise.challenge.models.MovieDetail
import com.highrise.challenge.models.MovieName
import com.highrise.challenge.models.Score

internal class JniMovieDetail(
    private val name: String,
    private val score: Float,
    private val actors: Array<JniActor>,
    private val description: String
) {
    fun toMovieDetail() = MovieDetail(
        MovieName(name),
        Score(score.toInt()),
        actors.map { it.toActor() },
        Description(description)
    )
}