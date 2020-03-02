package com.highrise.challenge.models

inline class Timestamp(val millis: Long)

data class Movie(
    val name: MovieName,
    val lastUpdated: Timestamp
)
