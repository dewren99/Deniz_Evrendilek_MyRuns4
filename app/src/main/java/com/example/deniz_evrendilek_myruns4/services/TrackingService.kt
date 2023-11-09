package com.example.deniz_evrendilek_myruns4.services

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.deniz_evrendilek_myruns4.R

class TrackingService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START_TRACKING -> start()

            STOP_TRACKING -> stop()
            else -> throw IllegalStateException(
                "Unsupported intent?.action, please pass " + "START_TRACKING or STOP_TRACKING"
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        setupNotification()
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

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        const val NOTIFICATION_IMPORTANCE = NotificationManager.IMPORTANCE_LOW
        const val NOTIFICATION_CHANNEL_ID = "MyRuns Tracking Service"
        const val NOTIFICATION_CHANNEL_NAME = "MyRuns Tracking Service"
        private const val FOREGROUND_ID = 1
        const val START_TRACKING = "START_TRACKING_SERVICE"
        const val STOP_TRACKING = "STOP_TRACKING_SERVICE"
    }
}