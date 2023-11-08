package com.example.deniz_evrendilek_myruns4.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ExerciseEntryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    /**
     * @source: https://canvas.sfu.ca/courses/80625/pages/room-database
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseEntryViewModel::class.java)) {
            return ExerciseEntryViewModel(context) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}