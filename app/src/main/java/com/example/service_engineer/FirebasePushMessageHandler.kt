package com.example.service_engineer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebasePushMessageHandler : FirebaseMessagingService() {
    private val CHANNEL_ID = "engineer-notification2"

    override fun onNewToken(token: String) {
        Log.d("fcm", "New Token: $token")
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("fcm", "Message received")
        val sharedPref = getSharedPreferences("login-data", MODE_PRIVATE)

        val loggedInId = sharedPref.getString("loggedInServiceEngineerId", "") ?: return

        val toEngineerId = remoteMessage.data["toEngineerId"]
        val destinationApp = remoteMessage.data["destination"]
        if (destinationApp != "engineer")
            return
        Log.d(
            "fcm",
            "Current phone number: '$loggedInId', received event for '$toEngineerId', " +
                    "should send: ${loggedInId == toEngineerId} ",
        )

        if (loggedInId == toEngineerId) {
            Log.d("fcm", "SEnding notification")
            showNotification()
        }
    }

    private fun showNotification() {
        createNotificationChannel()

        val goToNotifications = Intent(this, MainActivity2::class.java)
        goToNotifications.putExtra("is-notification", "true")

        val notifIntent = PendingIntent.getActivity(
            this,
            0,
            goToNotifications,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Service alloted")
            .setContentIntent(notifIntent)
            .setContentText("You have been alotted a service.")
            .setAutoCancel(true)
            .build()

        val notificationId = System.currentTimeMillis();

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@FirebasePushMessageHandler,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            } else {
                notify(notificationId.toInt(), notification)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Service Requests"
            val descriptionText = "Alerts the user when a service request is accepted"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}