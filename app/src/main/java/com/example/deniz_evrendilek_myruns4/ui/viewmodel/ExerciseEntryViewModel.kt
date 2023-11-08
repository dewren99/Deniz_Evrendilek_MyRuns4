package com.example.deniz_evrendilek_myruns4.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.deniz_evrendilek_myruns4.constants.PreferenceConstants
import com.example.deniz_evrendilek_myruns4.data.database.MainDatabase
import com.example.deniz_evrendilek_myruns4.data.model.ExerciseEntry
import com.example.deniz_evrendilek_myruns4.data.repository.ExerciseEntryRepository

class ExerciseEntryViewModel(
    context: Context
) : ViewModel() {
    private val _exerciseEntryRepository: ExerciseEntryRepository
    private val _allExerciseEntries: LiveData<List<ExerciseEntry>>
    private var _exerciseEntryDisplay: MutableLiveData<ExerciseEntry>
    private val _unitPreference: MutableLiveData<String>

    init {
        val db = MainDatabase.getInstance(context)
        val dao = db.exerciseEntryDao
        _exerciseEntryRepository = ExerciseEntryRepository(dao)
        _allExerciseEntries = _exerciseEntryRepository.getAll().asLiveData()
        _exerciseEntryDisplay = MutableLiveData()
        _unitPreference = MutableLiveData(PreferenceConstants.getUnit(context))
    }

    val allExerciseEntries
        get() = MediatorLiveData<Pair<List<ExerciseEntry>?, String?>>().apply {
            addSource(_allExerciseEntries) { value = it to _unitPreference.value }
            addSource(_unitPreference) { value = _allExerciseEntries.value to it }
        }

    val exerciseEntryDisplay
        get() = MediatorLiveData<Pair<ExerciseEntry?, String?>>().apply {
            addSource(_exerciseEntryDisplay) { value = it to _unitPreference.value }
            addSource(_unitPreference) { value = _exerciseEntryDisplay.value to it }
        }


    fun insert(exerciseEntry: ExerciseEntry) {
        _exerciseEntryRepository.insert(exerciseEntry)
    }

    fun delete(exerciseEntry: ExerciseEntry) {
        _exerciseEntryRepository.delete(exerciseEntry)
    }

    fun display(exerciseEntry: ExerciseEntry) {
        _exerciseEntryDisplay = MutableLiveData(exerciseEntry)
    }

    fun setUnitPreference(unit: String) {
        _unitPreference.value = unit
    }
}