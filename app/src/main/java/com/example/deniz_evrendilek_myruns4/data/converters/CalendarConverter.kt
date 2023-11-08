package com.example.deniz_evrendilek_myruns4.data.converters

import android.icu.util.Calendar
import androidx.room.TypeConverter

class CalendarConverter {
    @TypeConverter
    fun fromTimestamp(timestamp: Long?): Calendar? {
        if (timestamp == null) {
            return null
        }
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar
    }

    @TypeConverter
    fun toTimestamp(calendar: Calendar?): Long? = calendar?.timeInMillis
}