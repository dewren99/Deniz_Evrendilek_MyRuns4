package com.example.deniz_evrendilek_myruns4.data.model

import android.icu.util.Calendar
import android.location.Location
import com.example.deniz_evrendilek_myruns4.constants.ExerciseTypes.EXERCISE_TYPE_UNKNOWN_ID
import com.example.deniz_evrendilek_myruns4.constants.InputTypes.INPUT_TYPE_UNKNOWN_ID
import com.google.android.gms.maps.model.LatLng

data class TrackingExerciseEntry(
    val inputType: Int,
    val activityType: Int,
    val dateTime: Calendar,
    val duration: Double,
    val distance: Double,
    val avgPace: Double,
    val avgSpeed: Double,
    val calorie: Double,
    val climb: Double,
    val heartRate: Double,
    val comment: String,
    val locationList: List<Location>
) {

    constructor(
        inputType: Int,
        activityType: Int,
        dateTime: Calendar,
        locationList: List<Location>
    ) : this(
        inputType = inputType,
        activityType = activityType,
        dateTime = dateTime,
        duration = getTotalDuration(locationList),
        distance = getTotalDistance(locationList),
        avgPace = getAvgPace(locationList),
        avgSpeed = getAvgSpeed(locationList),
        calorie = getCaloriesBurnt(locationList),
        climb = getTotalClimb(locationList),
        heartRate = 0.0,
        comment = "",
        locationList = locationList
    )

    fun getCurrentSpeed(): String {
        val na = "n/a"
        if (locationList.isEmpty()) {
            return na
        }
        val last = locationList.last()
        return if (last.hasSpeed()) "${last.speed}m/s" else na
    }

    fun toExerciseEntry(): ExerciseEntry {
        if (inputType == INPUT_TYPE_UNKNOWN_ID) {
            throw IllegalStateException("Input type cannot be unknown")
        }

        return ExerciseEntry(
            inputType = inputType,
            activityType = activityType,
            dateTime = dateTime,
            duration = duration,
            distance = distance,
            avgPace = avgPace,
            avgSpeed = avgSpeed,
            calorie = calorie,
            climb = climb,
            heartRate = heartRate,
            comment = comment,
            locationList = locationToLatLngList(locationList)
        )
    }

    companion object {
        fun emptyTrackingExerciseEntry(): TrackingExerciseEntry {
            return TrackingExerciseEntry(
                inputType = INPUT_TYPE_UNKNOWN_ID,
                activityType = EXERCISE_TYPE_UNKNOWN_ID,
                dateTime = Calendar.getInstance(),
                locationList = listOf()
            )
        }

        fun getAvgSpeed(locationList: List<Location>): Double {
            if (locationList.size < 2) {
                return 0.0
            }

            val speedDataExists = locationList.all { it.hasSpeed() }
            if (speedDataExists) {
                return locationList.map { it.speed }.average()
            }

            val totalDistance = getTotalDistance(locationList)
            val totalDuration = getTotalDuration(locationList)
            val totalTimeHours = totalDuration / 3600.0
            if (totalTimeHours <= 0) {
                return 0.0
            }
            return totalDistance / totalTimeHours
        }

        fun getAvgPace(locationList: List<Location>): Double {
            if (locationList.size < 2) {
                return 0.0
            }
            val totalDistance = getTotalDistance(locationList)
            val totalDuration = getTotalDuration(locationList)
            val totalTimeHours = totalDuration / 3600.0
            if (totalTimeHours <= 0) {
                return 0.0
            }
            return totalTimeHours / (totalDistance / 1000.0) // 1609.34 for mile
        }

        fun getTotalDuration(locationList: List<Location>): Double {
            if (locationList.size < 2) {
                return 0.0
            }

            val startTime = locationList.first().time.toDouble()
            val endTime = locationList.last().time.toDouble()
            return (endTime - startTime) / 1000.0 // in seconds
        }

        fun getTotalDistance(locationList: List<Location>): Double {
            if (locationList.size < 2) {
                return 0.0
            }
            return locationList.zipWithNext { a, b -> a.distanceTo(b) }.sum().toDouble()
        }

        /**
         * https://blog.nasm.org/metabolic-equivalents-for-weight-loss
         */
        fun getCaloriesBurnt(locationList: List<Location>): Double {
            val metConst = 3.0
            val avgHumanWeightKgCanada = 77.0
            val calPerMin = (metConst * avgHumanWeightKgCanada * 3.5) / 200.0
            return calPerMin * (getTotalDuration(locationList) * 60.0)
        }

        fun getTotalClimb(locationList: List<Location>): Double {
            if (locationList.size < 2) {
                return 0.0
            }

            return locationList.zipWithNext { a, b ->
                (b.altitude - a.altitude).takeIf { it > 0.0 } ?: 0.0
            }.sum() // in meters
        }

        fun locationToLatLngList(locationList: List<Location>): ArrayList<LatLng> {
            val arrayList = arrayListOf<LatLng>()
            locationList.forEach {
                val latLng = LatLng(it.latitude, it.longitude)
                arrayList.add(latLng)
            }
            return arrayList
        }
    }
}
