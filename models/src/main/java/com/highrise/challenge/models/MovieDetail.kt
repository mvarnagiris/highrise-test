package com.highrise.challenge.models

inline class Score(val value: Int)
inline class Description(val value: String)

data class MovieDetail(
    val name: MovieName,
    val score: Score,
    val actors: List<Actor>,
    val description: Description
)
