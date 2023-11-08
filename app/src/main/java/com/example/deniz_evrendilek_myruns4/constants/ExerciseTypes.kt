package com.example.deniz_evrendilek_myruns4.constants

import android.content.Context
import com.example.deniz_evrendilek_myruns4.R

object ExerciseTypes {
    private lateinit var _types: Array<String>
    private lateinit var _typesWithIntIds: Map<String, Int>
    fun init(context: Context) {
        _types = context.resources.getStringArray(R.array.ActivityType)
        val temp = mutableMapOf<String, Int>()
        var i = 0
        _types.forEach {
            temp[it] = i
            i++
        }
        _typesWithIntIds = temp
    }

    fun getString(index: Int) = _types[index]
    fun getId(type: String) = _typesWithIntIds[type]
}