package com.example.deniz_evrendilek_myruns4.ui.fragments.navigations

import android.content.Intent
import android.graphics.Color
import android.location.Location
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
    private var coordinates = mutableListOf<Location>()
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
        Intent(requireActivity().applicationContext, TrackingService::class.java).apply {
            action = TrackingService.START
            requireActivity().startService(this)
        }
        subscribeToTrackingService()
    }

    private fun subscribeToTrackingService() {
        TrackingService.trackedCoordinates.observe(viewLifecycleOwner) {
            onCoordinatesUpdated(it)
        }
    }

    private fun onCoordinatesUpdated(updatedCoordinates: MutableList<Location>) {
        if (updatedCoordinates.isNotEmpty()) {
            println(
                "Coordinate update: (${updatedCoordinates.last().latitude},${
                    updatedCoordinates.last().longitude
                })"
            )
        }
        coordinates = updatedCoordinates
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
        coordinates.forEach {
            val latLng = LatLng(it.latitude, it.longitude)
            polylineOptions.add(latLng)
        }
        polylineOptions.color(Color.BLACK)
        googleMap.addPolyline(polylineOptions)
    }

    private fun addMarkerInitialLocation() {
        if (coordinates.isEmpty()) {
            return
        }
        markerInitialLocation?.remove()
        val first = coordinates.first()
        val latLng = LatLng(first.latitude, first.longitude)
        markerInitialLocation = googleMap.addMarker(
            MarkerOptions().position(latLng).title("Start Location")
        )
    }

    private fun addMarkerCurrentLocation() {
        if (coordinates.isEmpty()) {
            return
        }
        markerCurrentLocation?.remove()
        val last = coordinates.last()
        val latLng = LatLng(last.latitude, last.longitude)
        markerCurrentLocation = googleMap.addMarker(
            MarkerOptions().position(latLng).title("Current Location")
        )
    }

    private fun focusMapToCurrentLocation() {
        val latLng = markerCurrentLocation?.position ?: return
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        googleMap.animateCamera(cameraUpdate)
    }

    private fun setStatTexts() {
        val type = exerciseType ?: "Unknown"
        view.findViewById<TextView>(R.id.map_exercise_type).text = "Type: $type"
        view.findViewById<TextView>(R.id.map_exercise_avg_speed).text = "Avg speed: "
        view.findViewById<TextView>(R.id.map_exercise_curr_speed).text = "Curr speed: "
        view.findViewById<TextView>(R.id.map_exercise_climb).text = "Climb: "
        view.findViewById<TextView>(R.id.map_exercise_calories).text = "Calories: "
        view.findViewById<TextView>(R.id.map_exercise_distance).text = "Distance: "
    }


    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        startTrackingService()
    }
}