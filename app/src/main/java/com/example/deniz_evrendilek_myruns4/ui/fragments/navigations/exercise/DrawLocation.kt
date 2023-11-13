package com.example.deniz_evrendilek_myruns4.ui.fragments.navigations.exercise

import android.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class DrawLocation(
    private val locations: List<LatLng>, private val googleMap: GoogleMap
) {
    fun draw() {
        addMarkerInitialLocation()
        drawPolyline()
        addMarkerCurrentLocation()
        focusMapToCurrentLocation()
    }

    private fun drawPolyline() {
        val polylineOptions = PolylineOptions()
        locations.forEach {
            polylineOptions.add(it)
        }
        polylineOptions.color(Color.BLACK)
        polyline?.remove()
        polyline = googleMap.addPolyline(polylineOptions)
    }

    private fun addMarkerInitialLocation() {
        if (locations.isEmpty()) {
            return
        }
        markerInitialLocation?.remove()
        val first = locations.first()
        markerInitialLocation = googleMap.addMarker(
            MarkerOptions().position(first).title("Start Location")
        )
    }

    private fun addMarkerCurrentLocation() {
        if (locations.isEmpty()) {
            return
        }
        markerCurrentLocation?.remove()
        val last = locations.last()
        markerCurrentLocation = googleMap.addMarker(
            MarkerOptions().position(last).title("Current Location")
        )
    }

    private fun focusMapToCurrentLocation() {
        val latLng = markerCurrentLocation?.position ?: return
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM)
        googleMap.animateCamera(cameraUpdate)
    }

    companion object {
        private const val ZOOM = 16f
        private var markerInitialLocation: Marker? = null
        private var markerCurrentLocation: Marker? = null
        private var polyline: Polyline? = null
    }
}