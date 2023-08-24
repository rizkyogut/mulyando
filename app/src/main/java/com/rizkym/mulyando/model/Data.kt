package com.rizkym.mulyando.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Data(
    val userName: String? = null,
    val phoneUser: String? = null,
    val place: String? = null,
    val address: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val nameMachine: String? = null,
    val diagnosisMachine: String? = null,
	val status: String? = null,
	val timeCreated: String? = null,
    val user_timeCreated: String? = null,
    val perbaikan: Perbaikan? = null
) : Parcelable

