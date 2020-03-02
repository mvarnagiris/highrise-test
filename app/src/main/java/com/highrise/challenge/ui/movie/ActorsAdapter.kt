package com.highrise.challenge.ui.movie

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.highrise.challenge.R
import com.highrise.challenge.extensions.inflate
import com.highrise.challenge.extensions.load
import com.highrise.challenge.models.Actor
import com.highrise.challenge.ui.ImageLoader.Transformation.CIRCLE_CROP
import com.highrise.challenge.ui.movie.ActorsAdapter.ActorViewHolder

class ActorsAdapter : Adapter<ActorViewHolder>() {
    var actors = emptyList<Actor>()
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun getItemCount(): Int = actors.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ActorViewHolder(parent.inflate(R.layout.actor_item_view))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        val actor = actors[position]
        holder.nameAndAgeTextView.text = "${actor.name.value}, ${actor.age.value}"
        holder.photoImageView.load(
            actor.photo,
            placeholderResId = R.drawable.photo_placeholder,
            transformation = CIRCLE_CROP
        )
    }

    class ActorViewHolder(itemView: View) : ViewHolder(itemView) {
        val nameAndAgeTextView: TextView = itemView.findViewById(R.id.nameAndAgeTextView)
        val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)
    }
}