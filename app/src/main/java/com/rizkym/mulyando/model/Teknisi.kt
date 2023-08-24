package com.rizkym.mulyando.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Teknisi(
    val name: String? = null,
    val phoneNumber: String? = null,
    val url: String? = null,
    val image: String? = null
) : Parcelable {

}
