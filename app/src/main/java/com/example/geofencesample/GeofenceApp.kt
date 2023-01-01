package com.example.geofencesample

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import java.util.Date

class GeofenceApp : Application() {

    val logs: MutableList<Pair<String, Date>> = mutableListOf()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { lockscreenVisibility = Notification.VISIBILITY_PUBLIC }

        val notificationManager =
            (getSystemService(NotificationManager::class.java) as NotificationManager)

        notificationManager.createNotificationChannel(channel)
    }
}