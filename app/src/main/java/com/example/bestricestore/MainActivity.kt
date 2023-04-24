package com.example.bestricestore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bestricestore.data.Constants
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_ORDER)
        setContentView(R.layout.activity_main)
    }
}