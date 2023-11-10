package com.example.deniz_evrendilek_myruns4.services

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.deniz_evrendilek_myruns4.R
import com.example.deniz_evrendilek_myruns4.managers.LocationTrackingManager
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TrackingService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationTrackingManager: LocationTrackingManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val fusedLocationProvider =
            LocationServices.getFusedLocationProviderClient(applicationContext)
        locationTrackingManager = LocationTrackingManager(applicationContext, fusedLocationProvider)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START -> start()
            STOP -> stop()
            else -> throw IllegalStateException(
                "Unsupported intent?.action, please pass " + "START_TRACKING or STOP_TRACKING"
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        setupNotification()
        setupLocationListener()
    }

    private fun setupNotification() {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notification.setContentTitle("MyRuns").setContentText("Recording your path now")
            .setSmallIcon(R.drawable.el_gato_drawable).setOngoing(true).setAutoCancel(false)

//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(FOREGROUND_ID, notification.build())

        startForeground(FOREGROUND_ID, notification.build())
    }

    private fun setupLocationListener() {
        locationTrackingManager.subscribe(LOCATION_POLL_INTERVAL).catch { it.printStackTrace() }
            .onEach {
                println("${it.latitude},${it.longitude}")
            }.launchIn(scope)
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        private const val LOCATION_POLL_INTERVAL = 5000L
        const val NOTIFICATION_IMPORTANCE = NotificationManager.IMPORTANCE_LOW
        const val NOTIFICATION_CHANNEL_ID = "MyRuns Tracking Service"
        const val NOTIFICATION_CHANNEL_NAME = "MyRuns Tracking Service"
        private const val FOREGROUND_ID = 1
        const val START = "START_TRACKING_SERVICE"
        const val STOP = "STOP_TRACKING_SERVICE"
    }
}