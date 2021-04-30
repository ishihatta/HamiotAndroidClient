package com.ishihata_tech.hamiot_client.usecase

import android.util.Log
import com.ishihata_tech.hamiot_client.repo.FirebaseFunctionsRepository
import com.ishihata_tech.hamiot_client.repo.UserAccountRepository
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CreateNewAccountImpl @Inject constructor(
    private val userAccountRepository: UserAccountRepository,
    private val firebaseFunctionsRepository: FirebaseFunctionsRepository,
) : CreateNewAccount {
    companion object {
        private const val TAG = "CreateNewAccountImpl"
    }

    override suspend fun invoke(publicKey: String, displayName: String, fcmToken: String): Boolean {
        return suspendCoroutine { coroutine ->
            firebaseFunctionsRepository.functions
                .getHttpsCallable("newAccount")
                .call(hashMapOf(
                    "publicKey" to publicKey,
                    "displayName" to displayName,
                    "fcmToken" to fcmToken
                ))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        Log.d(TAG, "onComplete: ${task.result?.data}")
                        (task.result?.data as? Map<*, *>)?.also { data ->
                            val resultCode = data["result"]
                            if (resultCode == "OK") {
                                val accountId = (data["accountId"] as? String) ?: ""
                                val irohaAddress = (data["irohaAddress"] as? String) ?: ""
                                if (accountId.isNotEmpty() && irohaAddress.isNotEmpty()) {
                                    userAccountRepository.accountId = accountId
                                    userAccountRepository.displayName = displayName
                                    userAccountRepository.irohaAddress = irohaAddress
                                    coroutine.resume(true)
                                    return@addOnCompleteListener
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "onComplete: failure: ${task.exception}")
                    }
                    // 失敗
                    coroutine.resume(false)
                }
        }
    }
}