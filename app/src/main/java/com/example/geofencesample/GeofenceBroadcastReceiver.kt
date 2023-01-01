package com.example.geofencesample

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import java.util.Date

const val RECEIVE_TEST_ACTION = "com.example.geofencesample.RECEIVE_TEST_ACTION"
const val CHANNEL_ID = "channel_id"

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val app = (context?.applicationContext as? GeofenceApp)

        if (intent?.action == RECEIVE_TEST_ACTION) {
            sendTestNotification(context!!)
            return
        }

        app?.logs?.add("intent: $intent" to Date())

        intent ?: return

        val geofenceEvent = GeofencingEvent.fromIntent(intent)

        app?.logs?.add("geofenceEvent: $geofenceEvent" to Date())

        if (geofenceEvent?.hasError() == true) {
            val message = GeofenceStatusCodes.getStatusCodeString(geofenceEvent.errorCode)
            app?.logs?.add(message to Date())
            return
        }

        val geofenceTransition = geofenceEvent?.geofenceTransition
        /**
         * int GEOFENCE_TRANSITION_ENTER = 1;
        int GEOFENCE_TRANSITION_EXIT = 2;
        int GEOFENCE_TRANSITION_DWELL = 4;
        long NEVER_EXPIRE = -1L;
         */

        val tran = when {
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER -> "Enter"
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT -> "Exit"
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL -> "Dwell"
            else -> "Unknown"
        }

        app?.logs?.add("geofenceTransition: $geofenceTransition" to Date())

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {
            val triggerGeofences: List<Geofence>? = geofenceEvent.triggeringGeofences
            val body = getDetails(geofenceTransition, triggerGeofences ?: emptyList())
            app?.logs?.add(body to Date())
            sendNotification(context!!, body)
        } else {
            app?.logs?.add("Enter/Exit 以外" to Date())
        }
    }

    private fun getDetails(transition: Int, geofences: List<Geofence>): String {

        val tr = when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "ジオフェンス内に入りました"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "ジオフェンスから出ました"
            Geofence.GEOFENCE_TRANSITION_DWELL -> "ジオフェンス内です"
            else -> "Unknown"
        }

        val ids = geofences.joinToString(",") { it.requestId }

        return "$tr : $ids"
    }

    private fun sendTestNotification(context: Context) {
        sendNotification(context, "Notification Test")
    }

    private fun sendNotification(context: Context, body: String) {

        val title = context.getString(R.string.app_name)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle(title)
            setContentText(body)
            setWhen(System.currentTimeMillis())
            setDefaults(Notification.DEFAULT_ALL)
            setAutoCancel(true)
        }

        builder.setContentIntent(
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

        NotificationManagerCompat
            .from(context)
            .notify(R.string.app_name, builder.build())

        (context.applicationContext as GeofenceApp).logs.add("Send notification" to Date())
    }
}