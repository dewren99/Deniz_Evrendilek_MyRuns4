package com.example.deniz_evrendilek_myruns4.ui.fragments.navigations.exercise.interfaces

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

interface MapFragmentInterface : OnMapReadyCallback {
    var mapFragment: SupportMapFragment
    var googleMap: GoogleMap
    fun initViewModels()
    fun initViewModelObservers()
    fun initMap()
}