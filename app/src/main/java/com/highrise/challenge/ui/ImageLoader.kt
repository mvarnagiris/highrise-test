package com.highrise.challenge.ui

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.highrise.challenge.models.Image
import com.highrise.challenge.ui.ImageLoader.Transformation.CIRCLE_CROP

object ImageLoader {
    fun load(imageView: ImageView, image: Image?, @DrawableRes placeholderResId: Int? = null, transformation: Transformation? = null) {
        Glide.with(imageView)
            .load(image?.url)
            .let {
                if (transformation != null) when (transformation) {
                    CIRCLE_CROP -> it.circleCrop()
                } else it
            }
            .let {
                if (placeholderResId != null) {
                    it.placeholder(placeholderResId)
                } else {
                    it
                }
            }
            .into(imageView)
    }

    enum class Transformation { CIRCLE_CROP }
}