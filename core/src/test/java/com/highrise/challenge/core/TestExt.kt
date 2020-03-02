package com.highrise.challenge.core

import kotlinx.coroutines.CompletableDeferred

fun <T> deferred() = CompletableDeferred<T>()