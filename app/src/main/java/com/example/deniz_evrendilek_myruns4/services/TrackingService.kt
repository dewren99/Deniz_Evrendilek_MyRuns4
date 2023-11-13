package com.example.deniz_evrendilek_myruns4.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.deniz_evrendilek_myruns4.R
import com.example.deniz_evrendilek_myruns4.constants.ExerciseTypes.EXERCISE_TYPE_UNKNOWN_ID
import com.example.deniz_evrendilek_myruns4.constants.InputTypes.INPUT_TYPE_UNKNOWN_ID
import com.example.deniz_evrendilek_myruns4.data.model.TrackingExerciseEntry
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
    private var exerciseTypeId: Int = EXERCISE_TYPE_UNKNOWN_ID
    private var inputTypeId: Int = INPUT_TYPE_UNKNOWN_ID

    override fun onBind(intent: Intent?): IBinder? {
        // TODO: onBind vs onStartCommand, look at starting notification here
        return null
    }

    override fun onCreate() {
        super.onCreate()
        initLocationProvider()
        resetTrackedExerciseEntry()
    }

    private fun initLocationProvider() {
        val fusedLocationProvider =
            LocationServices.getFusedLocationProviderClient(applicationContext)
        locationTrackingManager = LocationTrackingManager(applicationContext, fusedLocationProvider)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START -> {
                exerciseTypeId = intent.getIntExtra("EXERCISE_TYPE_ID", EXERCISE_TYPE_UNKNOWN_ID)
                inputTypeId = intent.getIntExtra("INPUT_TYPE_ID", INPUT_TYPE_UNKNOWN_ID)
                start()
            }

            STOP -> stop()
            else -> throw IllegalStateException(
                "Unsupported intent?.action ${intent?.action}, please pass " + "START or STOP"
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        setupNotification()
        setupLocationListener()
    }

    private fun setupNotification() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            println("Cannot create Notification Channel, Android SDK is too old")
            return
        }
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NOTIFICATION_IMPORTANCE
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notification.setContentTitle("MyRuns")
            .setContentText("Recording your path now")
            .setSmallIcon(R.drawable.el_gato_drawable)
            .setOngoing(true)
            .setAutoCancel(false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notification.foregroundServiceBehavior = Notification.FOREGROUND_SERVICE_IMMEDIATE
        }

        startForeground(FOREGROUND_ID, notification.build())
    }

    private fun setupLocationListener() {
        locationTrackingManager.subscribe(LOCATION_POLL_INTERVAL).catch { it.printStackTrace() }
            .onEach {
                onLocationUpdate(it)
            }.launchIn(scope)
    }

    private fun onLocationUpdate(location: Location) {
        addToTrackedExerciseData(inputTypeId, exerciseTypeId, location)
        println("Tracking Service: ${location.latitude},${location.longitude}")
    }

    private fun stop() {
        resetTrackedExerciseEntry()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        private const val LOCATION_POLL_INTERVAL = 1000L
        const val NOTIFICATION_IMPORTANCE = NotificationManager.IMPORTANCE_LOW
        const val NOTIFICATION_CHANNEL_ID = "MyRuns Tracking Service"
        const val NOTIFICATION_CHANNEL_NAME = "MyRuns Tracking Service"
        private const val FOREGROUND_ID = 1
        const val START = "START_TRACKING_SERVICE"
        const val STOP = "STOP_TRACKING_SERVICE"

        val trackedExerciseEntry = MutableLiveData<TrackingExerciseEntry>()

        private fun resetTrackedExerciseEntry() {
            trackedExerciseEntry.postValue(TrackingExerciseEntry.emptyTrackingExerciseEntry())
        }

        private fun addToTrackedExerciseData(
            inputType: Int, exerciseType: Int, location:
            Location
        ) {
            val entry = trackedExerciseEntry.value ?: return
            val locations = entry.locationList.toMutableList()
            locations.add(location)
            val update = TrackingExerciseEntry(
                inputType = inputType,
                activityType = exerciseType,
                dateTime = entry.dateTime,
                locationList = locations.toList()
            )
            trackedExerciseEntry.postValue(update)
        }
    }
}