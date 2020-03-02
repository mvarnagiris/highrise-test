@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.highrise.challenge.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.coroutines.CoroutineContext

class CoroutinesTestRule : TestWatcher(), CoroutineScope {
    private val testDispatcher = TestCoroutineDispatcher()
    override val coroutineContext: CoroutineContext = Job() + testDispatcher

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        cancel()
        testDispatcher.cleanupTestCoroutines()
        Dispatchers.resetMain()
    }

//    fun runBlockingTestt(block: suspend TestCoroutineScope.() -> Unit) {
//        runBlockingTest(testCoroutinesScope.coroutineContext, block)
//    }
}