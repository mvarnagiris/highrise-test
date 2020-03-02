@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.highrise.challenge.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

abstract class Mvi<INPUT, STATE>(initialState: STATE) : CoroutineScope, Closeable {
    override val coroutineContext: CoroutineContext by lazy { SupervisorJob() + Dispatchers.Main }

    private val inputsChannel by lazy { Channel<INPUT>(Channel.UNLIMITED) }
    private val statesChannel by lazy { ConflatedBroadcastChannel(initialState) }

    val state: STATE get() = statesChannel.value
    val states: Flow<STATE> get() = statesChannel.asFlow()

    init {
        launch {
            for (input in inputsChannel) {
                handleInput(input).collect { setState(it) }
            }
        }
    }

    fun input(input: INPUT): Boolean {
        if (inputsChannel.isClosedForSend) return false
        inputsChannel.offer(input)
        return true
    }

    protected abstract fun handleInput(input: INPUT): Flow<STATE>

    private fun setState(state: STATE) {
        val oldState = this.state
        if (state != oldState) {
            statesChannel.offer(state)
        }
    }

    override fun close() {
        inputsChannel.close()
        statesChannel.close()
        coroutineContext.cancel()
    }
}
