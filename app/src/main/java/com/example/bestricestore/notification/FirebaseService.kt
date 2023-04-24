package com.example.bestricestore.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.bestricestore.MainActivity
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseService : com.google.firebase.messaging.FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = Random.nextInt()   //make sure diff Id to make many notifications at the same time

         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){    //check version if Oreo
             createNotificationChannel(notificationManager)
         }

        val  pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_ONE_SHOT)
        //FLAG ONE SHOT - the notification only uses once
        val notification = NotificationCompat.Builder(this, Constants.CHANNEL_ID)   //now modify
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setAutoCancel(true)    // auto delete noti when we click
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(notificationManager: NotificationManager){
        val  channelName = "Order_notification"
        val  channel = NotificationChannel(Constants.CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            //modify channel
            description = "channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
    }
}