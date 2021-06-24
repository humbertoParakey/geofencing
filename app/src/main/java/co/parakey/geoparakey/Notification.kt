package co.parakey.geoparakey

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object Notification {
    private const val CHANNEL_ID = "geoChannel"
    private const val TAG = "Notification"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Geo Channel"
            val descriptionText = "The channel to which show the geo notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(context: Context?, text: String, content: String){
        Log.i(TAG, text)
        val builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setContentTitle(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText(content)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openDoorPendingIntent(context))
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            val notificationId = (1..10000).random()
            notify(notificationId, builder.build())
        }
    }

    private fun openDoorPendingIntent(context: Context?): PendingIntent{
        val intent = Intent(context, NfcActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val developmentDoorBleURL = "NFC DOOR URI GOES HERE"
        intent.data = Uri.parse(developmentDoorBleURL)
        return PendingIntent.getActivity(context, 0, intent, 0)
    }
}
