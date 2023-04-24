package com.example.bestricestore.notification

data class PushNotification(
    val data: NotificationData,
    val to: String  //the receiver
) {
}