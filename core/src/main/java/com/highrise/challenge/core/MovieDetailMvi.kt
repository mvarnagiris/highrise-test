package com.highrise.challenge.core

import com.highrise.challenge.core.MovieDetailMvi.Input
import com.highrise.challenge.core.MovieDetailMvi.Input.Refresh
import com.highrise.challenge.core.MovieDetailMvi.State
import com.highrise.challenge.core.MovieDetailMvi.State.Empty
import com.highrise.challenge.core.MovieDetailMvi.State.Failed
import com.highrise.challenge.core.MovieDetailMvi.State.Loaded
import com.highrise.challenge.core.MovieDetailMvi.State.Loading
import com.highrise.challenge.models.MovieDetail
import com.highrise.challenge.models.MovieName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieDetailMvi(
    private val movieName: MovieName,
    private val movieDetailSource: MovieDetailSource
) : Mvi<Input, State>(Empty(movieName)) {

    init {
        refresh()
    }

    fun refresh() = input(Refresh)

    override fun handleInput(input: Input): Flow<State> {
        return when (input) {
            Refresh -> flow {
                emit(Loading(movieName, state.movieDetail))

                try {
                    val movieDetail = movieDetailSource.getMovieDetail(movieName)
                    if (movieDetail == null)
                        emit(Empty(movieName))
                    else
                        emit(Loaded(movieDetail))
                } catch (e: Exception) {
                    emit(Failed(movieName, state.movieDetail, e))
                }
            }
        }
    }

    sealed class Input {
        internal object Refresh : Input()
    }

    sealed class State {
        abstract val movieName: MovieName
        abstract val movieDetail: MovieDetail?
        val humanReadableScore: String? get() = movieDetail
            ?.let { "${it.score.value}" }

        data class Loading(
            override val movieName: MovieName,
            override val movieDetail: MovieDetail?
        ) : State()

        data class Loaded(
            override val movieDetail: MovieDetail
        ) : State() {
            override val movieName: MovieName get() = movieDetail.name
        }

        data class Failed(
            override val movieName: MovieName,
            override val movieDetail: MovieDetail?,
            val cause: Exception
        ) : State()

        data class Empty(override val movieName: MovieName) : State() {
            override val movieDetail: MovieDetail? get() = null
        }
    }
}