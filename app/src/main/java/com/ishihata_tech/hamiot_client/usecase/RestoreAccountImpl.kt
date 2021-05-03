package com.ishihata_tech.hamiot_client.usecase

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ishihata_tech.hamiot_client.repo.UserAccountRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class RestoreAccountImpl @Inject constructor(
        @ApplicationContext private val context: Context,
        private val userAccountRepository: UserAccountRepository,
) : RestoreAccount {
    companion object {
        private const val TAG = "RestoreAccountImpl"
    }

    override fun invoke(uri: Uri): Boolean {
        // ファイルを読み込む
        val jsonString = context.contentResolver.openInputStream(uri)?.use {
            if (it.available() < 64 * 1024) {
                it.readBytes().toString(StandardCharsets.UTF_8)
            } else null
        } ?: return false

        Log.d(TAG, "jsonString=$jsonString")

        // JSONパース
        val json = try {
            JSONObject(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Failure to parse json")
            return false
        }

        return try {
            val publicKey = json.getString("publicKey")
            val privateKey = json.getString("privateKey")
            val accountId = json.getString("accountId")
            val displayName = json.getString("displayName")
            val irohaAddress = json.getString("irohaAddress")

            userAccountRepository.publicKey = publicKey
            userAccountRepository.privateKey = privateKey
            userAccountRepository.accountId = accountId
            userAccountRepository.displayName = displayName
            userAccountRepository.irohaAddress = irohaAddress

            true
        } catch (e: Exception) {
            Log.e(TAG, "Failure to get parameters from backup file")
            false
        }
    }
}