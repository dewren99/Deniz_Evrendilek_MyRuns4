package com.example.deniz_evrendilek_myruns4.ui.fragments.navigations

import android.content.Intent
import android.graphics.Color
import android.icu.math.BigDecimal
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
import com.example.deniz_evrendilek_myruns4.constants.PreferenceConstants
import com.example.deniz_evrendilek_myruns4.data.model.TrackingExerciseEntry
import com.example.deniz_evrendilek_myruns4.services.TrackingService
import com.example.deniz_evrendilek_myruns4.ui.viewmodel.StartFragmentViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions


const val MAP_HEADER = "Map"

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var view: View
    private lateinit var buttonCancel: Button
    private lateinit var buttonSave: Button
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap
    private lateinit var trackingExerciseEntry: TrackingExerciseEntry
    private var markerInitialLocation: Marker? = null
    private var markerCurrentLocation: Marker? = null
    private lateinit var startFragmentViewModel: StartFragmentViewModel
    private var exerciseType: String? = null
    private var inputType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false)

        startFragmentViewModel =
            ViewModelProvider(requireActivity())[StartFragmentViewModel::class.java]
        startFragmentViewModel.inputAndActivityType.observe(viewLifecycleOwner) {
            inputType = it.first

            if (inputType == "GPS") {
                exerciseType = it.second
            }
        }

        setToolbarHeader()
        setupButtons()
        setupMap()

        return view
    }

    private fun startTrackingService() {
        if (inputType == null) {
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
        if (trackingExerciseEntry.locationList.isNotEmpty()) {
            println(
                "Coordinate update: (${trackingExerciseEntry.locationList.last().latitude},${
                    trackingExerciseEntry.locationList.last().longitude
                })"
            )
        }
        this.trackingExerciseEntry = trackingExerciseEntry
        drawTravelPath()
        setStatTexts()
    }

    private fun setupMap() {
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
            onExit()
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

    private fun onExit() {
        restoreToolbarHeader()
        Intent(requireActivity().applicationContext, TrackingService::class.java).apply {
            action = TrackingService.STOP
            requireActivity().startService(this)
        }
    }

    private fun drawTravelPath() {
        addMarkerInitialLocation()
        drawPolyline()
        addMarkerCurrentLocation()
        focusMapToCurrentLocation()
    }

    private fun drawPolyline() {
        val polylineOptions = PolylineOptions()
        trackingExerciseEntry.locationList.forEach {
            val latLng = LatLng(it.latitude, it.longitude)
            polylineOptions.add(latLng)
        }
        polylineOptions.color(Color.BLACK)
        googleMap.addPolyline(polylineOptions)
    }

    private fun addMarkerInitialLocation() {
        if (trackingExerciseEntry.locationList.isEmpty()) {
            return
        }
        markerInitialLocation?.remove()
        val first = trackingExerciseEntry.locationList.first()
        val latLng = LatLng(first.latitude, first.longitude)
        markerInitialLocation = googleMap.addMarker(
            MarkerOptions().position(latLng).title("Start Location")
        )
    }

    private fun addMarkerCurrentLocation() {
        if (trackingExerciseEntry.locationList.isEmpty()) {
            return
        }
        markerCurrentLocation?.remove()
        val last = trackingExerciseEntry.locationList.last()
        val latLng = LatLng(last.latitude, last.longitude)
        markerCurrentLocation = googleMap.addMarker(
            MarkerOptions().position(latLng).title("Current Location")
        )
    }

    private fun focusMapToCurrentLocation() {
        val latLng = markerCurrentLocation?.position ?: return
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
        googleMap.animateCamera(cameraUpdate)
    }

    private fun setStatTexts() {
        val exerciseTypeTextView = view.findViewById<TextView>(R.id.map_exercise_type)
        val caloriesTextView = view.findViewById<TextView>(R.id.map_exercise_calories)

        val type = exerciseType ?: "Unknown"
        val typeText = "Type: $type"
        val caloriesText = "Calories: ${trackingExerciseEntry.calorie.toInt()}"

        exerciseTypeTextView.text = typeText
        caloriesTextView.text = caloriesText
        when (PreferenceConstants.getUnit(requireContext())) {
            PreferenceConstants.UNIT_PREFERENCE_METRIC -> setStatsInMetric()
            PreferenceConstants.UNIT_PREFERENCE_IMPERIAL -> setStatsInImperial()
        }
    }

    private fun roundValues(double: Double): Double {
        return BigDecimal(double).setScale(2, BigDecimal.ROUND_HALF_EVEN).toDouble()
    }

    private fun setStatsInMetric() {
        val avgSpeedTextView = view.findViewById<TextView>(R.id.map_exercise_avg_speed)
        val currSpeedTextView = view.findViewById<TextView>(R.id.map_exercise_curr_speed)
        val climbTextView = view.findViewById<TextView>(R.id.map_exercise_climb)
        val distanceTextView = view.findViewById<TextView>(R.id.map_exercise_distance)

        val distanceAdjusted = roundValues(trackingExerciseEntry.distance / 1000)
        val distanceText = "Distance: $distanceAdjusted Kilometers"
        distanceTextView.text = distanceText

        val climbAdjusted = roundValues(trackingExerciseEntry.climb / 1000)
        val climbText = "Climb: $climbAdjusted Kilometers"
        climbTextView.text = climbText

        val avgSpeedAdjusted = roundValues(trackingExerciseEntry.avgSpeed * 3.6)
        val avgSpeedText = "Avg speed: $avgSpeedAdjusted km/h"
        avgSpeedTextView.text = avgSpeedText

        var currSpeedAdjusted = "n/a"
        if (trackingExerciseEntry.getCurrentSpeed() != null) {
            currSpeedAdjusted =
                roundValues(trackingExerciseEntry.getCurrentSpeed()!! * 3.6).toString() + "km/h"
        }
        val currSpeedText = "Curr speed: $currSpeedAdjusted"
        currSpeedTextView.text = currSpeedText
    }

    private fun setStatsInImperial() {
        val avgSpeedTextView = view.findViewById<TextView>(R.id.map_exercise_avg_speed)
        val currSpeedTextView = view.findViewById<TextView>(R.id.map_exercise_curr_speed)
        val climbTextView = view.findViewById<TextView>(R.id.map_exercise_climb)
        val distanceTextView = view.findViewById<TextView>(R.id.map_exercise_distance)

        val distanceAdjusted = roundValues(trackingExerciseEntry.distance / 1609)
        val distanceText = "Distance: $distanceAdjusted Miles"
        distanceTextView.text = distanceText

        val climbAdjusted = roundValues(trackingExerciseEntry.climb / 1609)
        val climbText = "Climb: $climbAdjusted Miles"
        climbTextView.text = climbText

        val avgSpeedAdjusted = roundValues(trackingExerciseEntry.avgSpeed * 2.23694)
        val avgSpeedText = "Avg speed: $avgSpeedAdjusted m/h"
        avgSpeedTextView.text = avgSpeedText

        var currSpeedAdjusted = "n/a"
        if (trackingExerciseEntry.getCurrentSpeed() != null) {
            currSpeedAdjusted =
                roundValues(trackingExerciseEntry.getCurrentSpeed()!! * 2.23694).toString() + "m/h"
        }
        val currSpeedText = "Curr speed: $currSpeedAdjusted"
        currSpeedTextView.text = currSpeedText
    }


    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        startTrackingService()
    }

    override fun onDestroyView() {
        println("onDestroyView on MapFragment")
        super.onDestroyView()
        googleMap.clear() // Replace 'googleMap' with your GoogleMap instance variable
        unsubscribeFromTrackingService()
    }
}