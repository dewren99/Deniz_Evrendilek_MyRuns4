package com.example.deniz_evrendilek_myruns4.managers

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch


/**
 * https://stackoverflow.com/a/33023788/5895675
 * Google’s Location Services API is a part of the Google Play Services APK. They’re built on top
 * of Android’s API. These APIs provide a “Fused Location Provider” instead of the providers mentioned
 * above. This provider automatically chooses what underlying provider to use, based on accuracy,
 * battery usage, etc. It is fast because you get location from a system-wide service that keeps
 * updating it. And you can use more advanced features such as geofencing.
 */
class LocationTrackingManager(
    private val context: Context, private val fusedLocationProvider: FusedLocationProviderClient
) {
    fun subscribe(interval: Long): Flow<Location> {
        return callbackFlow {
            subscribeInner(this, interval)
        }
    }

    private suspend fun subscribeInner(scope: ProducerScope<Location>, interval: Long) {
        hasPermissions()
        initLocationManager()
        initLocationProvider(scope, interval)
    }

    @SuppressLint("MissingPermission")
    private fun initLocationManager() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        hasProviders(isGpsEnabled, isNetworkEnabled)
    }

    @SuppressLint("MissingPermission")
    private suspend fun initLocationProvider(
        scope: ProducerScope<Location>,
        interval: Long
    ) {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
        val callback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                if (p0.lastLocation == null) {
                    return
                }
                scope.launch {
                    scope.send(p0.lastLocation!!)
                }
            }
        }
        fusedLocationProvider.requestLocationUpdates(
            request.build(), callback, Looper.getMainLooper()
        )

        scope.awaitClose {
            fusedLocationProvider.removeLocationUpdates(callback)
        }
    }

    private fun hasProviders(gps: Boolean, network: Boolean) {
        if (!gps && !network) {
            throw SecurityException(
                "LocationTrackingManager: GPS_PROVIDER and GPS_PROVIDER are not enabled"
            )
        }
    }

    private fun hasPermissions() {
        val permissions = ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!permissions) {
            throw SecurityException(
                "LocationTrackingManager is missing permission to use ACCESS_FINE_LOCATION or " +
                        "ACCESS_COARSE_LOCATION"
            )
        }
    }
}