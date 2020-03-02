@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.highrise.challenge.core

import com.highrise.challenge.core.MoviesMvi.State
import com.highrise.challenge.core.MoviesMvi.State.Empty
import com.highrise.challenge.core.MoviesMvi.State.Failed
import com.highrise.challenge.core.MoviesMvi.State.Loaded
import com.highrise.challenge.core.MoviesMvi.State.Loading
import com.highrise.challenge.models.Movie
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test

class MoviesMviTest {

    @get:Rule
    val scope = CoroutinesTestRule()

    private val stateCollector = mockk<(State) -> Unit>(relaxed = true)
    private val movies = listOf<Movie>(mockk(), mockk())
    private val refreshedMovies = listOf<Movie>(mockk(), mockk(), mockk())
    private val moviesSource = mockk<MoviesSource>()
    private val moviesMvi by lazy { MoviesMvi(moviesSource) }

    @Test
    fun `automatically loads movies on initial creation`() {
        val deferredMovies = deferred<List<Movie>>()
        coEvery { moviesSource.getMovies() } coAnswers { deferredMovies.await() }

        scope.launch { moviesMvi.states.collect { stateCollector(it) } }
        deferredMovies.complete(movies)

        verifyOrder {
            stateCollector(Loading(emptyList()))
            stateCollector(Loaded(movies))
        }
    }

    @Test
    fun `can refresh movies`() {
        coEvery { moviesSource.getMovies() } returnsMany listOf(movies, refreshedMovies)

        scope.launch { moviesMvi.states.collect { stateCollector(it) } }
        moviesMvi.refresh()

        verifyOrder {
            stateCollector(Loaded(movies))
            stateCollector(Loading(movies))
            stateCollector(Loaded(refreshedMovies))
        }
    }

    @Test
    fun `goes to empty state when returned movies list is empty`() {
        coEvery { moviesSource.getMovies() } returns emptyList()

        scope.launch { moviesMvi.states.collect { stateCollector(it) } }

        verify {
            stateCollector(Empty)
        }
    }

    @Test
    fun `handles failures`() {
        val error = RuntimeException()
        coEvery { moviesSource.getMovies() } returns movies
        scope.launch { moviesMvi.states.collect { stateCollector(it) } }

        coEvery { moviesSource.getMovies() } throws error
        moviesMvi.refresh()
        coEvery { moviesSource.getMovies() } returns refreshedMovies
        moviesMvi.refresh()

        verifyOrder {
            stateCollector(Loaded(movies))
            stateCollector(Loading(movies))
            stateCollector(Failed(movies, error))
            stateCollector(Loading(movies))
            stateCollector(Loaded(refreshedMovies))
        }
    }
}