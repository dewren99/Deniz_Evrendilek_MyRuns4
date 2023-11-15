package com.example.deniz_evrendilek_myruns4.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
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
import com.example.deniz_evrendilek_myruns4.ui.activities.MainActivity
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TrackingService : Service() {
    private var isFirstRun = true

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationTrackingManager: LocationTrackingManager
    private var exerciseTypeId: Int = EXERCISE_TYPE_UNKNOWN_ID
    private var inputTypeId: Int = INPUT_TYPE_UNKNOWN_ID
    private lateinit var onNotificationClickIntent: PendingIntent

    override fun onBind(intent: Intent?): IBinder? {
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
            START -> run {
                if (!isFirstRun) {
                    return@run
                }
                isFirstRun = false
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
        setOnClickNotificationIntent() // must be set before setupNotification
        setupNotification()
        setupLocationListener()
    }

    private fun setupNotificationChannel() {
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
    }

    /**
     * We need a re-buildable notification to update onNotificationClickIntent
     */
    private fun getNotificationBuilder(): NotificationCompat.Builder {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notification.setContentTitle("MyRuns").setContentText("Recording your path now")
            .setSmallIcon(R.drawable.el_gato_drawable).setOngoing(true).setAutoCancel(false)
            .setContentIntent(onNotificationClickIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notification.foregroundServiceBehavior = Notification.FOREGROUND_SERVICE_IMMEDIATE
        }
        return notification
    }

    private fun setupNotification() {
        setupNotificationChannel()

        val notification = getNotificationBuilder()
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
    }

    /**
     * Handle the Intent for when notification bar is clicked
     */
    private fun setOnClickNotificationIntent() {
        onNotificationClickIntent = PendingIntent.getActivity(
            this, ON_NOTIFICATION_CLICK_REQUEST, Intent(
                this, MainActivity::class.java
            ).apply {
                action = ON_NOTIFICATION_CLICK_ACTION
                // If the notification was clicked while app was already closed
                // InputType & ExerciseType needs to be passed back.
                // We cannot get these data otherwise
                putExtra("EXERCISE_TYPE_ID", exerciseTypeId)
                putExtra("INPUT_TYPE_ID", inputTypeId)
            },
            // https://stackoverflow.com/a/73368521/5895675
            FLAG_MUTABLE or FLAG_UPDATE_CURRENT
        )
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
        const val ON_NOTIFICATION_CLICK_REQUEST = 0
        const val ON_NOTIFICATION_CLICK_ACTION = "ON_NOTIFICATION_CLICK"

        val trackedExerciseEntry = MutableLiveData<TrackingExerciseEntry>()

        private fun resetTrackedExerciseEntry() {
            trackedExerciseEntry.postValue(TrackingExerciseEntry.emptyTrackingExerciseEntry())
        }

        private fun addToTrackedExerciseData(
            inputType: Int, exerciseType: Int, location: Location
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