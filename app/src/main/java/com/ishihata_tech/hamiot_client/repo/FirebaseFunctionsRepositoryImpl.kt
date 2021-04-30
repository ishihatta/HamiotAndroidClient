package com.ishihata_tech.hamiot_client.repo

import com.google.firebase.FirebaseApp
import com.google.firebase.functions.FirebaseFunctions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseFunctionsRepositoryImpl @Inject constructor() : FirebaseFunctionsRepository {
    override val functions: FirebaseFunctions
        get() {
            val app = FirebaseApp.getInstance()
            return FirebaseFunctions.getInstance(app, "asia-northeast1").apply {
                // Use emulator
                useEmulator("10.0.2.2", 5001)
                //useEmulator("192.168.10.102", 5001)
            }
        }
}