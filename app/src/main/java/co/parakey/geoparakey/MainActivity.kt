package co.parakey.geoparakey

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    private var geofencingClient: GeofencingClient? = null
    private val listOfGeofences = mutableListOf<Geofence>()
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Notification.createNotificationChannel(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
        createEntreportGeofence()
        initGeofences()
    }

    private fun createEntreportGeofence(){
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient?.addGeofences(geofencingRequest(), geofencePendingIntent)?.run {
                addOnSuccessListener {
                    Toast.makeText(this@MainActivity, "Geofences added", Toast.LENGTH_LONG).show()
                }
                addOnFailureListener {
                    Toast.makeText(this@MainActivity, "Unable to create geofences", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this@MainActivity, "Please enable location permission", Toast.LENGTH_LONG).show()
        }
    }

    private fun geofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(listOfGeofences)
        }.build()
    }

    companion object {
        private const val REQUEST_ID = "Parakey"
        private const val PARAKEY_LAT = 57.70566592293835
        private const val PARAKEY_LONG = 11.966665356381512
        private const val GEO_RADIUS_METERS = 100f
    }
}
