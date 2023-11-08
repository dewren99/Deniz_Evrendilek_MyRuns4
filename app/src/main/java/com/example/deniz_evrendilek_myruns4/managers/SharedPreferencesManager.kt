package com.example.deniz_evrendilek_myruns4.managers

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(preferenceKey: String, context: Context) {
    companion object {
        const val PROFILE_PREFERENCES = "PROFILE_PREFERENCES"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE)

    @Suppress("unused")
    fun saveValue(key: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun saveValue(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getValue(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun getValue(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue)!!
    }
}