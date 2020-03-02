package com.highrise.challenge.core

import com.highrise.challenge.core.MoviesMvi.Input
import com.highrise.challenge.core.MoviesMvi.Input.Refresh
import com.highrise.challenge.core.MoviesMvi.State
import com.highrise.challenge.core.MoviesMvi.State.Empty
import com.highrise.challenge.core.MoviesMvi.State.Failed
import com.highrise.challenge.core.MoviesMvi.State.Loaded
import com.highrise.challenge.core.MoviesMvi.State.Loading
import com.highrise.challenge.models.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MoviesMvi(private val moviesSource: MoviesSource) : Mvi<Input, State>(Empty) {

    init {
        refresh()
    }

    fun refresh() = input(Refresh)

    override fun handleInput(input: Input): Flow<State> {
        return when (input) {
            Refresh -> flow {
                emit(Loading(state.movies))

                try {
                    val movies = moviesSource.getMovies()
                    if (movies.isEmpty())
                        emit(Empty)
                    else
                        emit(Loaded(movies))
                } catch (e: Exception) {
                    emit(Failed(state.movies, e))
                }
            }
        }
    }

    sealed class Input {
        internal object Refresh : Input()
    }

    sealed class State {
        abstract val movies: List<Movie>

        data class Loading(override val movies: List<Movie>) : State()
        data class Loaded(override val movies: List<Movie>) : State()
        data class Failed(override val movies: List<Movie>, val cause: Exception) : State()
        object Empty : State() {
            override val movies: List<Movie> get() = emptyList()
        }
    }
}