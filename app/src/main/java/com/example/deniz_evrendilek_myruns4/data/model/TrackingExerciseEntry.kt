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
    /**
     * distance is in meters
     */
    val distance: Double,
    val avgPace: Double,
    /**
     * avgSpeed in meter per second
     */
    val avgSpeed: Double,
    val calorie: Double,
    /**
     * climb in meters
     */
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

    fun getCurrentSpeed(): Double? {
        if (locationList.isEmpty()) {
            return null
        }
        val last = locationList.last()
        if (!last.hasSpeed()) {
            return null
        }
        val speed = last.speed.toDouble()
        return speed
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

        /**
         * Speed in meter per second
         */
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
            val speed = (totalDistance / totalTimeHours)
            return speed
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
            val pace = totalTimeHours / (totalDistance / 1000.0) // 1609.34 for mile
            return pace
        }

        /**
         * Duration in seconds
         */
        fun getTotalDuration(locationList: List<Location>): Double {
            if (locationList.size < 2) {
                return 0.0
            }

            val startTime = locationList.first().time.toDouble()
            val endTime = locationList.last().time.toDouble()
            return (endTime - startTime) / 1000.0 // in seconds
        }

        /**
         * Distance in meters
         */
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
            var currentSpeed = 5.0
            if (locationList.isNotEmpty()) {
                currentSpeed = locationList.last().speed.toDouble()
            }
            val metConst = when {
                currentSpeed < 2.0 -> 2.0
                currentSpeed < 5.0 -> 3.0
                currentSpeed < 8.0 -> 6.0
                else -> 8.0  // Running fast
            }
            val avgHumanWeightKgCanada = 77.0
            val calPerMin = (metConst * avgHumanWeightKgCanada * 3.5) / 200.0
            val mins = getTotalDuration(locationList) / 60.0
            val calories = calPerMin * mins
            return calories

        }

        /**
         * Altitude in meters
         */
        fun getTotalClimb(locationList: List<Location>): Double {
            if (locationList.size < 2) {
                return 0.0
            }

            val climb = locationList.zipWithNext { a, b ->
                (b.altitude - a.altitude).takeIf { it > 0.0 } ?: 0.0
            }.sum() // in meters
            return climb
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
