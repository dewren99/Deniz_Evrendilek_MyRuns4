package com.example.deniz_evrendilek_myruns4.ui.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StartFragmentViewModel : ViewModel() {

    private val _inputType = MutableLiveData<String>()
    private val _activityType = MutableLiveData<String>()
    val inputType: MutableLiveData<String> get() = _inputType

    val inputAndActivityType = MediatorLiveData<Pair<String?, String?>>().apply {
        addSource(_inputType) { value = it to _activityType.value }
        addSource(_activityType) { value = _inputType.value to it }
    }

    fun setInputType(type: String) {
        _inputType.value = type
    }

    fun setActivityType(type: String) {
        _activityType.value = type
    }
}