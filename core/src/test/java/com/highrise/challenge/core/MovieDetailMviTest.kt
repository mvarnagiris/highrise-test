@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.highrise.challenge.core

import com.highrise.challenge.core.MovieDetailMvi.State
import com.highrise.challenge.core.MovieDetailMvi.State.Empty
import com.highrise.challenge.core.MovieDetailMvi.State.Failed
import com.highrise.challenge.core.MovieDetailMvi.State.Loaded
import com.highrise.challenge.core.MovieDetailMvi.State.Loading
import com.highrise.challenge.models.MovieDetail
import com.highrise.challenge.models.MovieName
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test

class MovieDetailMviTest {

    @get:Rule
    val scope = CoroutinesTestRule()

    private val stateCollector = mockk<(State) -> Unit>(relaxed = true)
    private val movieDetail = mockk<MovieDetail>()
    private val refreshedMovieDetail = mockk<MovieDetail>()
    private val movieName = mockk<MovieName>(relaxed = true)
    private val movieDetailSource = mockk<MovieDetailSource>()
    private val movieDetailMvi by lazy { MovieDetailMvi(movieName, movieDetailSource) }

    @Test
    fun `automatically loads movie detail`() {
        val deferredMovieDetail = deferred<MovieDetail>()
        coEvery { movieDetailSource.getMovieDetail(movieName) } coAnswers { deferredMovieDetail.await() }

        scope.launch { movieDetailMvi.states.collect { stateCollector(it) } }
        deferredMovieDetail.complete(movieDetail)

        verifyOrder {
            stateCollector(Loading(movieName, null))
            stateCollector(Loaded(movieDetail))
        }
    }

    @Test
    fun `can refresh movie detail`() {
        coEvery { movieDetailSource.getMovieDetail(movieName) } returnsMany listOf(
            movieDetail,
            refreshedMovieDetail
        )

        scope.launch { movieDetailMvi.states.collect { stateCollector(it) } }
        movieDetailMvi.refresh()

        verifyOrder {
            stateCollector(Loaded(movieDetail))
            stateCollector(Loading(movieName, movieDetail))
            stateCollector(Loaded(refreshedMovieDetail))
        }
    }

    @Test
    fun `goes to empty state when returned movie detail is null`() {
        coEvery { movieDetailSource.getMovieDetail(movieName) } returns null

        scope.launch { movieDetailMvi.states.collect { stateCollector(it) } }

        verify {
            stateCollector(Empty(movieName))
        }
    }

    @Test
    fun `handles failures`() {
        val error = RuntimeException()
        coEvery { movieDetailSource.getMovieDetail(movieName) } returns movieDetail
        scope.launch { movieDetailMvi.states.collect { stateCollector(it) } }

        coEvery { movieDetailSource.getMovieDetail(movieName) } throws error
        movieDetailMvi.refresh()
        coEvery { movieDetailSource.getMovieDetail(movieName) } returns refreshedMovieDetail
        movieDetailMvi.refresh()

        verifyOrder {
            stateCollector(Loaded(movieDetail))
            stateCollector(Loading(movieName, movieDetail))
            stateCollector(Failed(movieName, movieDetail, error))
            stateCollector(Loading(movieName, movieDetail))
            stateCollector(Loaded(refreshedMovieDetail))
        }
    }
}