package com.example.deniz_evrendilek_myruns4.ui.fragments.navigations.exercise.createexercise

import android.content.Intent
import android.os.Bundle
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
import com.example.deniz_evrendilek_myruns4.data.model.TrackingExerciseEntry
import com.example.deniz_evrendilek_myruns4.services.TrackingService
import com.example.deniz_evrendilek_myruns4.ui.fragments.navigations.exercise.DrawLocation
import com.example.deniz_evrendilek_myruns4.ui.fragments.navigations.exercise.LocationStatistics
import com.example.deniz_evrendilek_myruns4.ui.fragments.navigations.exercise.interfaces.MapFragmentInterface
import com.example.deniz_evrendilek_myruns4.ui.viewmodel.ExerciseEntryViewModel
import com.example.deniz_evrendilek_myruns4.ui.viewmodel.ExerciseEntryViewModelFactory
import com.example.deniz_evrendilek_myruns4.ui.viewmodel.StartFragmentViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment


const val MAP_HEADER = "Map"

class MapFragment : Fragment(), MapFragmentInterface {
    private lateinit var view: View
    private lateinit var buttonCancel: Button
    private lateinit var buttonSave: Button
    override lateinit var mapFragment: SupportMapFragment
    override lateinit var googleMap: GoogleMap
    private lateinit var trackingExerciseEntry: TrackingExerciseEntry
    private lateinit var startFragmentViewModel: StartFragmentViewModel
    private lateinit var exerciseEntryViewModel: ExerciseEntryViewModel
    private lateinit var exerciseEntryViewModelFactory: ExerciseEntryViewModelFactory
    private var exerciseType: String? = null
    private var inputType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false)

        initViewModels()
        initViewModelObservers()

        setToolbarHeader()
        setupButtons()
        initMap()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleNavFromNotificationClick(arguments)

    }

    private fun handleNavFromNotificationClick(arguments: Bundle?) {
        // determine if a notification click directed us here.
        // We don't require to check EXERCISE_TYPE_ID since "Automatic" input type
        // can give null exercise type.
        val notNavigatedFromNotificationClick =
            (arguments == null) || !arguments.containsKey("INPUT_TYPE_ID")
        if (notNavigatedFromNotificationClick) {
            return
        }

        val exerciseTypeId = arguments?.getInt("EXERCISE_TYPE_ID")
        val inputTypeId = arguments?.getInt("INPUT_TYPE_ID")

        inputTypeId?.let {
            inputType = InputTypes.getString(it)
        }
        exerciseTypeId?.let {
            exerciseType = ExerciseTypes.getString(it)
        }
    }

    override fun initViewModels() {
        startFragmentViewModel =
            ViewModelProvider(requireActivity())[StartFragmentViewModel::class.java]
        exerciseEntryViewModelFactory = ExerciseEntryViewModelFactory(requireActivity())
        exerciseEntryViewModel = ViewModelProvider(
            requireActivity(), exerciseEntryViewModelFactory
        )[ExerciseEntryViewModel::class.java]
    }

    override fun initViewModelObservers() {
        startFragmentViewModel.inputAndActivityType.observe(viewLifecycleOwner) {
            inputType = it.first

            if (inputType == "GPS") {
                exerciseType = it.second
            }
        }
    }

    private fun startTrackingService() {
        if (inputType == null) {
            println("Cannot Start Tracking Service, inputType missing!")
            return
        }
        val inputTypeId = InputTypes.getId(inputType!!)
        val exerciseTypeId = ExerciseTypes.getId(exerciseType)
        Intent(requireActivity().applicationContext, TrackingService::class.java).apply {
            putExtra("INPUT_TYPE_ID", inputTypeId)
            putExtra("EXERCISE_TYPE_ID", exerciseTypeId)
            action = TrackingService.START
            requireActivity().applicationContext.startService(this)
        }
        subscribeToTrackingService()
    }

    private fun subscribeToTrackingService() {
        TrackingService.trackedExerciseEntry.observe(viewLifecycleOwner) {
            onCoordinatesUpdated(it)
        }
    }

    private fun unsubscribeFromTrackingService() {
        TrackingService.trackedExerciseEntry.removeObservers(viewLifecycleOwner)
    }

    private fun onCoordinatesUpdated(trackingExerciseEntry: TrackingExerciseEntry) {
        this.trackingExerciseEntry = trackingExerciseEntry
        DrawLocation(trackingExerciseEntry.latLngList, googleMap).draw()
        setStatTexts()
    }

    override fun initMap() {
        mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupButtons() {
        buttonCancel = view.findViewById(R.id.map_cancel_button)
        buttonSave = view.findViewById(R.id.map_save_button)

        buttonCancel.setOnClickListener {
            onExit()
            findNavController().navigate(R.id.action_mapFragment_to_mainFragment)
        }
        buttonSave.setOnClickListener {
            onSave()
            findNavController().navigate(R.id.action_mapFragment_to_mainFragment)
        }
    }

    private fun setToolbarHeader() {
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title = MAP_HEADER
    }

    private fun restoreToolbarHeader() {
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title = resources.getString(
            R.string.myruns
        )
    }

    private fun onSave() {
        restoreToolbarHeader()
        Intent(requireActivity().applicationContext, TrackingService::class.java).apply {
            action = TrackingService.STOP
            requireActivity().startService(this)
        }
        val exerciseEntry = trackingExerciseEntry.toExerciseEntry()
        exerciseEntryViewModel.insert(exerciseEntry)
    }

    private fun onExit() {
        restoreToolbarHeader()
        Intent(requireActivity().applicationContext, TrackingService::class.java).apply {
            action = TrackingService.STOP
            requireActivity().startService(this)
        }
    }

    private fun setStatTexts() {
        val exerciseTypeTextView = view.findViewById<TextView>(R.id.map_exercise_type)
        val caloriesTextView = view.findViewById<TextView>(R.id.map_exercise_calories)
        val avgSpeedTextView = view.findViewById<TextView>(R.id.map_exercise_avg_speed)
        val currSpeedTextView = view.findViewById<TextView>(R.id.map_exercise_curr_speed)
        val climbTextView = view.findViewById<TextView>(R.id.map_exercise_climb)
        val distanceTextView = view.findViewById<TextView>(R.id.map_exercise_distance)

        val type = exerciseType ?: "Unknown"
        val typeText = "Type: $type"
        exerciseTypeTextView.text = typeText

        val caloriesText = "Calories: ${trackingExerciseEntry.calorie.toInt()}"
        caloriesTextView.text = caloriesText

        distanceTextView.text = LocationStatistics.distance(
            requireContext(), trackingExerciseEntry.distance, "Distance: "
        )
        climbTextView.text = LocationStatistics.distance(
            requireContext(), trackingExerciseEntry.climb, "Climb: "
        )
        avgSpeedTextView.text = LocationStatistics.distance(
            requireContext(), trackingExerciseEntry.avgSpeed, "Avg speed: "
        )

        var currSpeedText = "Curr speed: n/a"
        if (trackingExerciseEntry.getCurrentSpeed() != null) {
            currSpeedText = LocationStatistics.speed(
                requireContext(), trackingExerciseEntry.getCurrentSpeed()!!, "Curr speed: "
            )
        }
        currSpeedTextView.text = currSpeedText
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        startTrackingService()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        googleMap.clear()
        unsubscribeFromTrackingService()
    }
}