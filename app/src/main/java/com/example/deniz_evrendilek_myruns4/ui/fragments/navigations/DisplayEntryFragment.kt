package com.example.deniz_evrendilek_myruns4.ui.fragments.navigations

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.deniz_evrendilek_myruns4.R
import com.example.deniz_evrendilek_myruns4.constants.ExerciseTypes
import com.example.deniz_evrendilek_myruns4.constants.InputTypes
import com.example.deniz_evrendilek_myruns4.data.model.ExerciseEntry
import com.example.deniz_evrendilek_myruns4.data.model.ManualExerciseEntryForm
import com.example.deniz_evrendilek_myruns4.ui.viewmodel.ExerciseEntryViewModel
import com.example.deniz_evrendilek_myruns4.ui.viewmodel.ExerciseEntryViewModelFactory
import com.google.android.material.button.MaterialButton


class DisplayEntryFragment : Fragment() {
    private lateinit var view: View
    private lateinit var toolbar: Toolbar
    private lateinit var deleteButton: Button
    private lateinit var exerciseEntryViewModelFactory: ExerciseEntryViewModelFactory
    private lateinit var exerciseEntryViewModel: ExerciseEntryViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_display_entry, container, false)
        initDeleteButton()

        exerciseEntryViewModelFactory = ExerciseEntryViewModelFactory(requireActivity())
        exerciseEntryViewModel = ViewModelProvider(
            requireActivity(), exerciseEntryViewModelFactory
        )[ExerciseEntryViewModel::class.java]

        exerciseEntryViewModel.exerciseEntryDisplay.observe(viewLifecycleOwner) { (exerciseEntry,
                                                                                      unit
                                                                                  ) ->
            if (exerciseEntry == null || unit == null) {
                return@observe
            }
            fillTextViews(exerciseEntry, unit)
            initDeleteButtonListener(exerciseEntry)
        }


        return view
    }

    private fun deleteExerciseEntry(exerciseEntry: ExerciseEntry) {
        exerciseEntryViewModel.delete(exerciseEntry)
    }

    private fun initDeleteButton() {
        toolbar = requireActivity().findViewById(R.id.toolbar)
        deleteButton = MaterialButton(
            requireContext(), null, com.google.android.material.R.attr.materialButtonStyle
        )
        deleteButton.setBackgroundColor(resources.getColor(R.color.palette_1))
        deleteButton.setTextColor(Color.WHITE)
        deleteButton.text = "DELETE"

        val layoutParams = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.END

        toolbar.addView(deleteButton, layoutParams)
    }

    private fun initDeleteButtonListener(exerciseEntry: ExerciseEntry) {
        deleteButton.setOnClickListener { _ ->
            deleteExerciseEntry(exerciseEntry)
            findNavController().navigate(R.id.action_displayEntryFragment_to_mainFragment)
        }
    }

    private fun removeDeleteButton() {
        toolbar.removeView(deleteButton)
    }

    private fun fillTextViews(it: ExerciseEntry, unit: String) {
        val inputType = InputTypes.getString(it.inputType)
        val exerciseType = ExerciseTypes.getString(it.activityType)
        val dateTime = ManualExerciseEntryForm.getDateTimeStr(it)
        val duration = ManualExerciseEntryForm.getDurationStr(it)
        val distance = ManualExerciseEntryForm.getDistanceStr(unit, it)
        val calories = ManualExerciseEntryForm.getCaloriesStr(it)
        val heartRate = ManualExerciseEntryForm.getHeartRateStr(it)
        view.findViewById<TextView>(R.id.display_exercise_entry_input_type).text = inputType
        view.findViewById<TextView>(R.id.display_exercise_entry_exercise_type).text = exerciseType
        view.findViewById<TextView>(R.id.display_exercise_entry_date_time).text = dateTime
        view.findViewById<TextView>(R.id.display_exercise_entry_duration).text = duration
        view.findViewById<TextView>(R.id.display_exercise_entry_distance).text = distance
        view.findViewById<TextView>(R.id.display_exercise_entry_calories).text = calories
        view.findViewById<TextView>(R.id.display_exercise_entry_heart_rate).text = heartRate
    }

    override fun onDestroyView() {
        removeDeleteButton()
        super.onDestroyView()
    }
}