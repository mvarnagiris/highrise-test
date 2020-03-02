package com.highrise.challenge.data.models

import com.highrise.challenge.models.Actor
import com.highrise.challenge.models.ActorName
import com.highrise.challenge.models.Age
import com.highrise.challenge.models.Image

internal class JniActor(
    private val name: String,
    private val age: Int,
    private val imageUrl: String
) {
    fun toActor() = Actor(ActorName(name), Age(age), Image(imageUrl))
}