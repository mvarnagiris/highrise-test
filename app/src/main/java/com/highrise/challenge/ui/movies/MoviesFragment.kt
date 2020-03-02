package com.highrise.challenge.ui.movies

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.highrise.challenge.R.layout
import com.highrise.challenge.core.MoviesMvi.State.Empty
import com.highrise.challenge.core.MoviesMvi.State.Failed
import com.highrise.challenge.core.MoviesMvi.State.Loading
import com.highrise.challenge.extensions.callbacksOn
import com.highrise.challenge.ui.movies.MoviesModule.moviesMvi
import kotlinx.android.synthetic.main.movies_fragment.*
import life.shank.android.AutoScoped

class MoviesFragment : Fragment(layout.movies_fragment), AutoScoped {

    private val adapter by lazy {
        MoviesAdapter {
            findNavController().navigate(MoviesFragmentDirections.showMovie(it.name.value))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moviesMvi.callbacksOn(this) {
            onResume { mvi ->
                swipeRefreshLayout.setOnRefreshListener { mvi.refresh() }
                errorView.setOnClickListener { mvi.refresh() }
            }

            onPause {
                swipeRefreshLayout.setOnRefreshListener(null)
                errorView.setOnClickListener(null)
            }

            collectStatesOnResume { _, state ->
                adapter.movies = state.movies
                swipeRefreshLayout.isRefreshing = state is Loading
                emptyView.isVisible = state is Empty
                errorView.isVisible = state is Failed
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }
}