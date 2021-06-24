package co.parakey.geoparakey

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class Geofencer(private val context: Context) {

    private var geofencingClient: GeofencingClient? = null
    private val listOfGeofences = mutableListOf<Geofence>()
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun initGeofence() {
        Notification.createNotificationChannel(context)

        geofencingClient = LocationServices.getGeofencingClient(context)

        createEntreportGeofence()
        if (!areGeoFencesRegistered()) {
            initGeofences()
        }
    }

    private fun createEntreportGeofence() {
        val entrePortGeofence = buildGeofence(
            requestId = REQUEST_ID,
            latitude = PARAKEY_LAT,
            longitude = PARAKEY_LONG,
            radius = GEO_RADIUS_METERS
        )
        listOfGeofences.add(entrePortGeofence)
    }

    private fun buildGeofence(
        requestId: String,
        latitude: Double,
        longitude: Double,
        radius: Float
    ): Geofence {
        return Geofence.Builder()
            .setRequestId(requestId)
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
    }

    private fun initGeofences() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient?.addGeofences(geofencingRequest(), geofencePendingIntent)?.run {
                addOnSuccessListener {
                    Toast.makeText(context, "Geofences added", Toast.LENGTH_LONG).show()
                }
                addOnFailureListener {
                    Log.i(TAG, "$it")
                    Toast.makeText(context, "Unable to create geofences", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(context, "Please enable location permission", Toast.LENGTH_LONG).show()
        }
    }

    private fun geofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(listOfGeofences)
            setGeofencesAsActivated()
        }.build()
    }

    private fun areGeoFencesRegistered(): Boolean {
        return context.getSharedPreferences(GEO_PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(GEO_ACTIVATION_KEY, false)
    }

    private fun setGeofencesAsActivated() {
        context.getSharedPreferences(GEO_PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(GEO_ACTIVATION_KEY, true)
            .apply()
    }

    companion object {
        private const val REQUEST_ID = "Parakey"
        private const val PARAKEY_LAT = 57.70566592293835
        private const val PARAKEY_LONG = 11.966665356381512
        private const val GEO_RADIUS_METERS = 100f
        private const val GEO_PREF_NAME = "geoPref"
        private const val GEO_ACTIVATION_KEY = "isPresnet"
        private const val TAG = "Geofencer"
    }
}
