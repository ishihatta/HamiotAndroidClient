package com.ishihata_tech.hamiot_client.usecase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GetFcmTokenImpl @Inject constructor() : GetFcmToken {
    companion object {
        private const val TAG = "GetFcmTokenImpl"
    }

    override suspend fun invoke(): String? = suspendCoroutine {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                it.resume(task.result)
            } else {
                Log.d(TAG, "Fetching FCM registration token failed", task.exception)
                it.resume(null)
            }
        }
    }
}