package com.example.deniz_evrendilek_myruns4.utils

import android.content.Context
import android.text.format.DateFormat

object DateTimeUtils {
    var is24HourFormat: Boolean = false
        private set

    fun init(context: Context) {
        is24HourFormat = DateFormat.is24HourFormat(context)
    }
}