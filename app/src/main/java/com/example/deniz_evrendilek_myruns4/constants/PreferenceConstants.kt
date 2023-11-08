package com.example.deniz_evrendilek_myruns4.constants

import android.content.Context
import androidx.preference.PreferenceManager

object PreferenceConstants {
    const val UNIT_PREFERENCE_KEY = "UNIT_PREFERENCE"
    const val UNIT_PREFERENCE_METRIC = "METRIC"
    const val UNIT_PREFERENCE_IMPERIAL = "IMPERIAL"
    const val UNIT_PREFERENCE_DEFAULT = UNIT_PREFERENCE_IMPERIAL

    private fun milesToKm(miles: Double): Double {
        return miles * 1.609
    }

    fun metricValue(context: Context, value: Double): String {
        val unit = getUnit(context)

        val convertedValue = if (unit == UNIT_PREFERENCE_METRIC) milesToKm(value) else value
        val prefix = if (unit == UNIT_PREFERENCE_METRIC) "Kilometers" else "Miles"

        return "$convertedValue $prefix"
    }

    fun metricValue(unit: String, value: Double): String {
        val convertedValue = if (unit == UNIT_PREFERENCE_METRIC) milesToKm(value) else value
        val prefix = if (unit == UNIT_PREFERENCE_METRIC) "Kilometers" else "Miles"

        return "$convertedValue $prefix"
    }

    fun getUnit(context: Context): String {
        val pm = PreferenceManager.getDefaultSharedPreferences(context)
        return pm.getString(UNIT_PREFERENCE_KEY, UNIT_PREFERENCE_DEFAULT) ?: UNIT_PREFERENCE_DEFAULT
    }
}