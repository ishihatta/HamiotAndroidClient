package com.ishihata_tech.hamiot_client.repo

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserKeyPairRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
): UserKeyPairRepository {
    companion object {
        private const val SHARED_PREFERENCES_NAME = "UserKeyPairRepositoryImpl"
        private const val PUBLIC_KEY = "public_key"
        private const val PRIVATE_KEY = "private_key"
    }

    private val sharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    override var publicKey: String?
        get() = sharedPreferences.getString(PUBLIC_KEY, null)
        set(value) { sharedPreferences.edit { putString(PUBLIC_KEY, value) } }

    override var privateKey: String?
        get() = sharedPreferences.getString(PRIVATE_KEY, null)
        set(value) { sharedPreferences.edit { putString(PRIVATE_KEY, value) } }

    override fun clear() {
        sharedPreferences.edit { clear() }
    }
}