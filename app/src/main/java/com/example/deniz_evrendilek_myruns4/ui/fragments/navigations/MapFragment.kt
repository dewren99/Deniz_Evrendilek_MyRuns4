package com.example.deniz_evrendilek_myruns4.ui.fragments.navigations

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.deniz_evrendilek_myruns4.R
import com.example.deniz_evrendilek_myruns4.services.TrackingService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


const val MAP_HEADER = "Map"

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var view: View
    private lateinit var buttonCancel: Button
    private lateinit var buttonSave: Button
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var coordinates = mutableListOf<Location>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false)

        setToolbarHeader()
        setupButtons()
        setupMap()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        startTrackingService()

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

    private fun hasLocationPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            println("ACCESS_FINE_LOCATION permission not granted")
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            println("ACCESS_COARSE_LOCATION permission not granted")
            return false
        }
        return true
    }

    private fun onExit() {
        restoreToolbarHeader()
        Intent(requireActivity().applicationContext, TrackingService::class.java).apply {
            action = TrackingService.STOP
            requireActivity().startService(this)
        }
    }

    @SuppressLint("MissingPermission") // hasLocationPermissions()
    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0

        if (!hasLocationPermissions()) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)
                googleMap.addMarker(MarkerOptions().position(userLatLng).title("Your Location"))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15.0f))
            }
        }
    }
}