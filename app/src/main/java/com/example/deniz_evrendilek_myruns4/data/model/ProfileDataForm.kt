package com.example.deniz_evrendilek_myruns4.data.model

import android.content.Context
import com.example.deniz_evrendilek_myruns4.managers.SharedPreferencesManager

class ProfileDataForm(context: Context) {
    companion object {
        private const val prefix: String = "PROFILE_DATA_"
        val KEYS = KEY
    }

    object KEY {
        const val NAME = prefix + "NAME"
        const val EMAIL = prefix + "EMAIL"
        const val PHONE = prefix + "PHONE"
        const val GENDER = prefix + "GENDER"
        const val CLASS = prefix + "CLASS"
        const val MAJOR = prefix + "MAJOR"
        const val PROFILE_IMAGE_URI = prefix + "PROFILE_IMAGE_URI"
    }

    private var sharedPreferencesManager = SharedPreferencesManager(
        SharedPreferencesManager.PROFILE_PREFERENCES, context
    )
    private var name = sharedPreferencesManager.getValue(KEY.NAME)
    private var email = sharedPreferencesManager.getValue(KEY.EMAIL)
    private var phone = sharedPreferencesManager.getValue(KEY.PHONE)
    private var gender = sharedPreferencesManager.getValue(KEY.GENDER)
    private var personClass = sharedPreferencesManager.getValue(KEY.CLASS)
    private var major = sharedPreferencesManager.getValue(KEY.MAJOR)
    private var maybeProfileImageUri =
        sharedPreferencesManager.getValue(KEY.PROFILE_IMAGE_URI)

    fun load(): Map<String, String> {
        return mapOf(
            KEY.NAME to name,
            KEY.EMAIL to email,
            KEY.PHONE to phone,
            KEY.GENDER to gender,
            KEY.CLASS to personClass,
            KEY.MAJOR to major,
            KEY.PROFILE_IMAGE_URI to maybeProfileImageUri
        )
    }

    fun save(
        name: String,
        email: String,
        phone: String,
        gender: String,
        personClass: String,
        major: String,
        maybeProfileImageUri: String
    ) {
        sharedPreferencesManager.saveValue(KEY.NAME, name)
        sharedPreferencesManager.saveValue(KEY.EMAIL, email)
        sharedPreferencesManager.saveValue(KEY.PHONE, phone)
        sharedPreferencesManager.saveValue(KEY.GENDER, gender)
        sharedPreferencesManager.saveValue(KEY.CLASS, personClass)
        sharedPreferencesManager.saveValue(KEY.MAJOR, major)
        sharedPreferencesManager.saveValue(KEY.PROFILE_IMAGE_URI, maybeProfileImageUri)
    }
}