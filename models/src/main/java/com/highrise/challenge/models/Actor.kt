package com.highrise.challenge.models

inline class ActorName(val value: String)
inline class Age(val value: Int)
inline class Image(val url: String)

data class Actor(
    val name: ActorName,
    val age: Age,
    val photo: Image
)
