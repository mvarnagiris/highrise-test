package com.highrise.challenge.extensions

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.highrise.challenge.models.Image
import com.highrise.challenge.ui.ImageLoader
import com.highrise.challenge.ui.ImageLoader.Transformation

fun ImageView.load(
    image: Image?,
    @DrawableRes placeholderResId: Int? = null,
    transformation: Transformation? = null
) = ImageLoader.load(this, image, placeholderResId, transformation)