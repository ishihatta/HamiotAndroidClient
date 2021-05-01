package com.ishihata_tech.hamiot_client

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ishihata_tech.hamiot_client.notification.MyNotificationChannel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MyNotificationChannel.initializeNotificationChannel(this)
    }
}