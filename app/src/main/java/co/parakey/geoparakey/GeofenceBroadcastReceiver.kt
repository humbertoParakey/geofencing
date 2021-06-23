package co.parakey.geoparakey

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            sendNotification(context, "Error", errorMessage)
            return
        }

        when(geofencingEvent.geofenceTransition){
            Geofence.GEOFENCE_TRANSITION_ENTER -> handleGeofenceEnter(context, geofencingEvent)
            Geofence.GEOFENCE_TRANSITION_EXIT -> handleGeofenceExit(context, geofencingEvent)
            else -> handleGeofenceOther(context, geofencingEvent)
        }
    }

    private fun handleGeofenceEnter(context: Context?, geofencingEvent: GeofencingEvent) {
        val geofenceTriggered = geofencingEvent.triggeringGeofences.first()
        val details = "ENTER: ${geofenceTriggered.requestId}"

        sendNotification(context, details, "Hejsan, click to open the door")
    }

    private fun handleGeofenceExit(context: Context?, geofencingEvent: GeofencingEvent) {
        val geofenceTriggered = geofencingEvent.triggeringGeofences.first()
        val details = "EXIT: ${geofenceTriggered.requestId}"

        sendNotification(context, details, "Hej då!!")
    }

    private fun handleGeofenceOther(context: Context?, geofencingEvent: GeofencingEvent) {
        val geofenceTriggered = geofencingEvent.triggeringGeofences.first()
        val details = "OTHER: ${geofenceTriggered.requestId}"

        sendNotification(context, details, "There is some other trigger")
    }

    private fun sendNotification(context: Context?, text: String, content: String){
        Log.i(TAG, text)
        val builder = NotificationCompat.Builder(context!!, Notification.CHANNEL_ID)
            .setContentTitle(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText(content)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)) {
            val notificationId = (1..10000).random()
            notify(notificationId, builder.build())
        }
    }

    companion object {
        private const val TAG = "GeofenceBroadcastReceiver"
    }
}
