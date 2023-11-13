package com.example.deniz_evrendilek_myruns4.ui.fragments.navigations.exercise

import android.content.Context
import android.icu.math.BigDecimal
import com.example.deniz_evrendilek_myruns4.constants.PreferenceConstants

class LocationStatistics {
    companion object {
        private const val METER_PER_SEC_TO_MILES_PER_HOUR = 2.23694
        private const val METER_PER_SEC_TO_KM_PER_HOUR = 3.6
        private const val METER_TO_MILES = 1 / 1609.0
        private const val METER_TO_KM = 1 / 1000.0
        private const val MPH = "m/h"
        private const val KPH = "km/h"
        private const val KILOMETERS = "Kilometers"
        private const val MILES = "Miles"
        fun roundValues(double: Double): Double {
            return BigDecimal(double).setScale(2, BigDecimal.ROUND_HALF_EVEN).toDouble()
        }

        fun speed(context: Context, value: Double, prefix: String? = ""): String {
            val (c, suffix) = when (PreferenceConstants.getUnit(context)) {
                PreferenceConstants.UNIT_PREFERENCE_METRIC -> Pair(
                    METER_PER_SEC_TO_KM_PER_HOUR,
                    KPH
                )

                PreferenceConstants.UNIT_PREFERENCE_IMPERIAL -> Pair(
                    METER_PER_SEC_TO_MILES_PER_HOUR,
                    MPH
                )

                else -> throw IllegalArgumentException("Unit Preference not found")
            }
            val avgSpeedAdjusted = roundValues(value * c)
            return "$prefix$avgSpeedAdjusted $suffix"
        }

        fun distance(context: Context, value: Double, prefix: String? = ""): String {
            val (c, suffix) = when (PreferenceConstants.getUnit(context)) {
                PreferenceConstants.UNIT_PREFERENCE_METRIC -> Pair(METER_TO_KM, KILOMETERS)
                PreferenceConstants.UNIT_PREFERENCE_IMPERIAL -> Pair(METER_TO_MILES, MILES)
                else -> throw IllegalArgumentException("Unit Preference not found")
            }
            val distanceAdjusted = roundValues(value * c)
            return "$prefix$distanceAdjusted $suffix"
        }
    }
}