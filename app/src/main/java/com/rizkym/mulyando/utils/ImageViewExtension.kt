package com.rizkym.mulyando.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation

fun ImageView.setImageProfile(url: String?) {
    Glide.with(this.context)
        .load(url)
        .apply(RequestOptions.circleCropTransform())
        .into(this)
}

fun ImageView.setImageBackground(url: String?) {
    Glide.with(this.context)
        .load(url)
        .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
        .into(this)
}



