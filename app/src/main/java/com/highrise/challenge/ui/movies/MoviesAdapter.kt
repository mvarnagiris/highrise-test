package com.highrise.challenge.ui.movies

import android.text.format.DateUtils.FORMAT_ABBREV_ALL
import android.text.format.DateUtils.FORMAT_SHOW_DATE
import android.text.format.DateUtils.FORMAT_SHOW_TIME
import android.text.format.DateUtils.formatDateTime
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.highrise.challenge.R
import com.highrise.challenge.extensions.inflate
import com.highrise.challenge.models.Movie
import com.highrise.challenge.ui.movies.MoviesAdapter.MovieViewHolder

class MoviesAdapter(private val onMovieClick: (Movie) -> Unit) : Adapter<MovieViewHolder>() {
    var movies = emptyList<Movie>()
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun getItemCount(): Int = movies.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MovieViewHolder(parent.inflate(R.layout.movie_item_view))

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.movieNameTextView.text = movie.name.value
        holder.timestampTextVIew.text = formatDateTime(
            holder.itemView.context,
            movie.lastUpdated.millis,
            FORMAT_ABBREV_ALL or FORMAT_SHOW_TIME or FORMAT_SHOW_DATE
        )
        holder.itemView.setOnClickListener { onMovieClick(movie) }
    }

    class MovieViewHolder(itemView: View) : ViewHolder(itemView) {
        val movieNameTextView: TextView = itemView.findViewById(R.id.movieNameTextView)
        val timestampTextVIew: TextView = itemView.findViewById(R.id.timestampTextView)
    }
}