package co.parakey.geoparakey

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Notification.sendNotification(context, "Error", errorMessage)
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

        Notification.sendNotification(context, details, "Hejsan, click to open the door")
    }

    private fun handleGeofenceExit(context: Context?, geofencingEvent: GeofencingEvent) {
        val geofenceTriggered = geofencingEvent.triggeringGeofences.first()
        val details = "EXIT: ${geofenceTriggered.requestId}"

        Notification.sendNotification(context, details, "Hej då!!")
    }

    private fun handleGeofenceOther(context: Context?, geofencingEvent: GeofencingEvent) {
        val geofenceTriggered = geofencingEvent.triggeringGeofences.first()
        val details = "OTHER: ${geofenceTriggered.requestId}"

        Notification.sendNotification(context, details, "There is some other trigger")
    }
}
