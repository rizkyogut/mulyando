package com.rizkym.mulyando.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Perbaikan(
    val userID: String? = null,
    val perbaikan: String? = null,
    val url: String? = null,
    val image: String? = null
) : Parcelable
