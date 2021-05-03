package com.ishihata_tech.hamiot_client.usecase

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ishihata_tech.hamiot_client.repo.UserAccountRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class BackupAccountImpl @Inject constructor(
        @ApplicationContext private val context: Context,
        private val userAccountRepository: UserAccountRepository,
) : BackupAccount {
    companion object {
        private const val TAG = "BackupAccountImpl"
    }

    override fun invoke(uri: Uri): Boolean {
        // バックアップデータの作成
        val json = JSONObject().apply {
            put("publicKey", userAccountRepository.publicKey)
            put("privateKey", userAccountRepository.privateKey)
            put("accountId", userAccountRepository.accountId)
            put("displayName", userAccountRepository.displayName)
            put("irohaAddress", userAccountRepository.irohaAddress)
        }
        val backupData = json.toString()

        // ファイルに保存
        return try {
            context.contentResolver.openOutputStream(uri)?.use {
                it.write(backupData.toByteArray(StandardCharsets.UTF_8))
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failure to write file", e)
            false
        }
    }
}