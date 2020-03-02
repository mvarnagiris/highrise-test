package com.highrise.challenge.ui.movie

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.highrise.challenge.R
import com.highrise.challenge.core.MovieDetailMvi.State.Empty
import com.highrise.challenge.core.MovieDetailMvi.State.Failed
import com.highrise.challenge.core.MovieDetailMvi.State.Loading
import com.highrise.challenge.extensions.callbacksOn
import com.highrise.challenge.models.MovieName
import com.highrise.challenge.ui.movie.MovieModule.movieDetailMvi
import kotlinx.android.synthetic.main.movie_fragment.*
import life.shank.android.AutoScoped

class MovieFragment : Fragment(R.layout.movie_fragment), AutoScoped {

    private val movieName by lazy { MovieName(MovieFragmentArgs.fromBundle(requireArguments()).movieName) }
    private val actorsAdapter by lazy { ActorsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieDetailMvi.callbacksOn(this, movieName) {

            onResume { mvi ->
                swipeRefreshLayout.setOnRefreshListener { mvi.refresh() }
                errorView.setOnClickListener { mvi.refresh() }
            }

            onPause {
                swipeRefreshLayout.setOnRefreshListener(null)
                errorView.setOnClickListener(null)
            }

            collectStatesOnResume { _, state ->
                actorsAdapter.actors = state.movieDetail?.actors.orEmpty()
                movieNameTextView.text = state.movieName.value
                scoreTextView.text = state.humanReadableScore
                descriptionTextView.text = state.movieDetail?.description?.value
                swipeRefreshLayout.isRefreshing = state is Loading
                scoreOutOfTextView.isVisible = state.movieDetail != null
                emptyView.isVisible = state is Empty
                errorView.isVisible = state is Failed
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = actorsAdapter
    }

}