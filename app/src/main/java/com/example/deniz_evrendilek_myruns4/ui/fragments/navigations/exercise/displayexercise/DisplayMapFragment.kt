package com.example.deniz_evrendilek_myruns4.ui.fragments.navigations.exercise.displayexercise

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
import com.example.deniz_evrendilek_myruns4.data.model.ExerciseEntry
import com.example.deniz_evrendilek_myruns4.ui.fragments.navigations.exercise.DrawLocation
import com.example.deniz_evrendilek_myruns4.ui.fragments.navigations.exercise.LocationStatistics
import com.example.deniz_evrendilek_myruns4.ui.fragments.navigations.exercise.interfaces.MapFragmentInterface
import com.example.deniz_evrendilek_myruns4.ui.viewmodel.ExerciseEntryViewModel
import com.example.deniz_evrendilek_myruns4.ui.viewmodel.ExerciseEntryViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.button.MaterialButton

class DisplayMapFragment : Fragment(), MapFragmentInterface {
    private lateinit var view: View
    private lateinit var toolbar: Toolbar
    private lateinit var deleteButton: Button
    override lateinit var mapFragment: SupportMapFragment
    override lateinit var googleMap: GoogleMap
    private lateinit var exerciseEntryViewModelFactory: ExerciseEntryViewModelFactory
    private lateinit var exerciseEntryViewModel: ExerciseEntryViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_display_map, container, false)
        initDeleteButton()
        initViewModels()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
    }

    override fun initViewModels() {
        exerciseEntryViewModelFactory = ExerciseEntryViewModelFactory(requireActivity())
        exerciseEntryViewModel = ViewModelProvider(
            requireActivity(), exerciseEntryViewModelFactory
        )[ExerciseEntryViewModel::class.java]


    }

    /**
     * Depends on googleMap
     */
    override fun initViewModelObservers() {
        exerciseEntryViewModel.exerciseEntryDisplay.observe(viewLifecycleOwner) { (exerciseEntry, unit) ->
            if (exerciseEntry == null || unit == null) {
                return@observe
            }
            initDeleteButtonListener(exerciseEntry)
            DrawLocation(exerciseEntry.locationList, googleMap).draw()
            setStatTexts(exerciseEntry)
        }
    }

    private fun initDeleteButton() {
        toolbar = requireActivity().findViewById(R.id.toolbar)
        deleteButton = MaterialButton(
            requireContext(), null, com.google.android.material.R.attr.materialButtonStyle
        )
        @Suppress("DEPRECATION")
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
            findNavController().navigate(R.id.action_displayMapFragment_to_mainFragment)
        }
    }

    private fun removeDeleteButton() {
        toolbar.removeView(deleteButton)
    }

    private fun deleteExerciseEntry(exerciseEntry: ExerciseEntry) {
        exerciseEntryViewModel.delete(exerciseEntry)
    }

    private fun setStatTexts(exerciseEntry: ExerciseEntry) {
        val exerciseTypeTextView = view.findViewById<TextView>(R.id.display_map_exercise_type)
        val caloriesTextView = view.findViewById<TextView>(R.id.display_map_exercise_calories)
        val avgSpeedTextView = view.findViewById<TextView>(R.id.display_map_exercise_avg_speed)
        val currSpeedTextView = view.findViewById<TextView>(R.id.display_map_exercise_curr_speed)
        val climbTextView = view.findViewById<TextView>(R.id.display_map_exercise_climb)
        val distanceTextView = view.findViewById<TextView>(R.id.display_map_exercise_distance)

        val type = ExerciseTypes.getString(exerciseEntry.activityType)
        val typeText = "Type: $type"
        exerciseTypeTextView.text = typeText

        val caloriesText = "Calories: ${exerciseEntry.calorie.toInt()}"
        caloriesTextView.text = caloriesText

        distanceTextView.text = LocationStatistics.distance(
            requireContext(), exerciseEntry.distance, "Distance: "
        )
        climbTextView.text = LocationStatistics.distance(
            requireContext(), exerciseEntry.climb, "Climb: "
        )
        avgSpeedTextView.text = LocationStatistics.distance(
            requireContext(), exerciseEntry.avgSpeed, "Avg speed: "
        )

        val currSpeedText = "Curr speed: n/a"
        currSpeedTextView.text = currSpeedText
    }

    override fun initMap() {
        mapFragment =
            childFragmentManager.findFragmentById(R.id.display_map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onDestroyView() {
        removeDeleteButton()
        super.onDestroyView()
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        initViewModelObservers()
    }
}