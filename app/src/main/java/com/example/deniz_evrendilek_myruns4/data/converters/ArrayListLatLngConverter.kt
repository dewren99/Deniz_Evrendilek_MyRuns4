package com.example.deniz_evrendilek_myruns4.data.converters

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng

class ArrayListLatLngConverter {
    companion object {
        private const val LIST_SEPARATOR = ","
        private const val LATLNG_SEPARATOR = ":"
    }

    @TypeConverter
    fun fromArrayListLatLng(list: ArrayList<LatLng>?): String? {
        return list?.joinToString(LIST_SEPARATOR) { "${it.latitude}$LATLNG_SEPARATOR${it.longitude}" }
    }

    @TypeConverter
    fun toLatLngList(data: String?): ArrayList<LatLng>? {
        if (data == null) {
            return null
        }
        val arrayList = ArrayList<LatLng>()
        data.split(LIST_SEPARATOR).forEach { latLngStr ->
            val latLngArr = latLngStr.split(LATLNG_SEPARATOR)
            if (latLngArr.size == 2) {
                val lat = latLngArr[0]
                val lng = latLngArr[1]
                arrayList.add(LatLng(lat.toDouble(), lng.toDouble()))
            }
        }
        return arrayList
    }
}