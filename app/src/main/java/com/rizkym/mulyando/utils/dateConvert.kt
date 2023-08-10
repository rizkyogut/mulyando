package com.rizkym.mulyando.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FILENAME_FORMAT = "dd-MMM-yyyy HH:mm:ss"

fun Long.unixDateFormat(milliseconds: Long): String {
    return SimpleDateFormat(
        FILENAME_FORMAT, Locale.getDefault()
    ).format(milliseconds * 100)
}

fun getDateTime(s: String): String? {
    return try {
        val sdf = SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault())
        val netDate = Date(s.toLong() * 1000)
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}