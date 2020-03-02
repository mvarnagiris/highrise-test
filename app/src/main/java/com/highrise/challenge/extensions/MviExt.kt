package com.highrise.challenge.extensions

import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.highrise.challenge.core.Mvi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import life.shank.ScopedProvider0
import life.shank.ScopedProvider1
import life.shank.ScopedProvider2
import life.shank.ScopedProvider3
import life.shank.android.AutoScoped

// IGNORE THIS - This should be part of libraries that are in progress at the moment

fun <INPUT, STATE, MVI : Mvi<INPUT, STATE>> MVI.callbacksOn(
    lifecycleOwner: LifecycleOwner,
    callbacks: MviLifecycleCallbacks<INPUT, STATE, MVI>.() -> Unit
) {
    val mviCallbacks = MviLifecycleCallbacks<INPUT, STATE, MVI>()
    callbacks(mviCallbacks)

    if (mviCallbacks.hasAnyLifecycleCallbacks) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {

            private var onCreateJob: Job? = null
            private var onStartJob: Job? = null
            private var onResumeJob: Job? = null

            override fun onStateChanged(source: LifecycleOwner, event: Event) {
                @Suppress("NON_EXHAUSTIVE_WHEN")
                when (event) {
                    ON_CREATE -> {
                        mviCallbacks.onCreateBlock?.invoke(this@callbacksOn)
                        onCreateJob = mviCallbacks.collectStatesOnCreateBlock?.let { block ->
                            lifecycleOwner.lifecycle.coroutineScope.launch {
                                states.collect {
                                    block(
                                        this@callbacksOn,
                                        it
                                    )
                                }
                            }
                        }
                    }
                    ON_START -> {
                        mviCallbacks.onStartBlock?.invoke(this@callbacksOn)
                        onStartJob = mviCallbacks.collectStatesOnStartBlock?.let { block ->
                            lifecycleOwner.lifecycle.coroutineScope.launch {
                                states.collect {
                                    block(
                                        this@callbacksOn,
                                        it
                                    )
                                }
                            }
                        }
                    }
                    ON_RESUME -> {
                        mviCallbacks.onResumeBlock?.invoke(this@callbacksOn)
                        onResumeJob = mviCallbacks.collectStatesOnResumeBlock?.let { block ->
                            lifecycleOwner.lifecycle.coroutineScope.launch {
                                states.collect {
                                    block(
                                        this@callbacksOn,
                                        it
                                    )
                                }
                            }
                        }
                    }
                    ON_PAUSE -> {
                        onResumeJob?.cancel()
                        onResumeJob = null
                        mviCallbacks.onPauseBlock?.invoke(this@callbacksOn)
                    }
                    ON_STOP -> {
                        onStartJob?.cancel()
                        onStartJob = null
                        mviCallbacks.onStopBlock?.invoke(this@callbacksOn)
                    }
                    ON_DESTROY -> {
                        onCreateJob?.cancel()
                        onCreateJob = null
                        mviCallbacks.onDestroyBlock?.invoke(this@callbacksOn)
                    }
                }
            }
        })
    }
}

class MviLifecycleCallbacks<INPUT, STATE, MVI : Mvi<INPUT, STATE>> {
    internal var onCreateBlock: ((MVI) -> Unit)? = null
    internal var onStartBlock: ((MVI) -> Unit)? = null
    internal var onResumeBlock: ((MVI) -> Unit)? = null
    internal var onPauseBlock: ((MVI) -> Unit)? = null
    internal var onStopBlock: ((MVI) -> Unit)? = null
    internal var onDestroyBlock: ((MVI) -> Unit)? = null
    internal var collectStatesOnCreateBlock: (suspend (MVI, STATE) -> Unit)? = null
    internal var collectStatesOnStartBlock: (suspend (MVI, STATE) -> Unit)? = null
    internal var collectStatesOnResumeBlock: (suspend (MVI, STATE) -> Unit)? = null

    internal val hasAnyLifecycleCallbacks
        get() =
            onCreateBlock != null || onStartBlock != null || onResumeBlock != null || onPauseBlock != null || onStopBlock != null || onDestroyBlock != null

    fun onCreate(block: (MVI) -> Unit) {
        onCreateBlock = block
    }

    fun onStart(block: (MVI) -> Unit) {
        onStartBlock = block
    }

    fun onResume(block: (MVI) -> Unit) {
        onResumeBlock = block
    }

    fun onPause(block: (MVI) -> Unit) {
        onPauseBlock = block
    }

    fun onStop(block: (MVI) -> Unit) {
        onStopBlock = block
    }

    fun onDestroy(block: (MVI) -> Unit) {
        onDestroyBlock = block
    }

    fun collectStatesOnCreate(block: suspend (MVI, STATE) -> Unit) {
        collectStatesOnCreateBlock = block
    }

    fun collectStatesOnStart(block: suspend (MVI, STATE) -> Unit) {
        collectStatesOnStartBlock = block
    }

    fun collectStatesOnResume(block: suspend (MVI, STATE) -> Unit) {
        collectStatesOnResumeBlock = block
    }
}


fun <AUTO_SCOPED : AutoScoped, T> ScopedProvider0<T>.onReady(
    autoScoped: AUTO_SCOPED,
    block: (T) -> Unit
) = autoScoped.onScopeReady { block(invoke(it)) }

fun <A, AUTO_SCOPED : AutoScoped, T> ScopedProvider1<A, T>.onReady(
    autoScoped: AUTO_SCOPED,
    a: A,
    block: (T) -> Unit
) = autoScoped.onScopeReady { block(invoke(it, a)) }

// SHANK + MVI ScopedProvider0 ---------------------------------------------------------------------------------------------------------------------------------


fun <INPUT, STATE, VIEW, MVI : Mvi<INPUT, STATE>> ScopedProvider0<MVI>.callbacksOn(
    view: VIEW,
    callbacks: MviLifecycleCallbacks<INPUT, STATE, MVI>.() -> Unit
) where VIEW : LifecycleOwner, VIEW : AutoScoped = onReady(view) { it.callbacksOn(view, callbacks) }

// SHANK + MVI ScopedProvider1 ---------------------------------------------------------------------------------------------------------------------------------

fun <A, INPUT, STATE, VIEW, MVI : Mvi<INPUT, STATE>> ScopedProvider1<A, MVI>.callbacksOn(
    view: VIEW,
    a: A,
    callbacks: MviLifecycleCallbacks<INPUT, STATE, MVI>.() -> Unit
) where VIEW : LifecycleOwner, VIEW : AutoScoped =
    onReady(view, a) { it.callbacksOn(view, callbacks) }