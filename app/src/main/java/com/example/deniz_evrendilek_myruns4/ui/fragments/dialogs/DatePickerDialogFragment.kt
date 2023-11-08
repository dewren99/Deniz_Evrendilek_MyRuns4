package com.example.deniz_evrendilek_myruns4.ui.fragments.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment

interface DateListener {
    fun onDateSelected(year: Int, month: Int, dayOfMonth: Int)
}

class DatePickerDialogFragment(
    private val _year: Int, private val _month: Int, private val _day: Int
) : DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DatePickerDialog(requireContext(), this, _year, _month, _day)
    }

    @Suppress("DEPRECATION")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        (targetFragment as? DateListener)?.onDateSelected(year, month, dayOfMonth)
    }
}