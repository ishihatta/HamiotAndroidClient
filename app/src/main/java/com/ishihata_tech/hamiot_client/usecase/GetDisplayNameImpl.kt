package com.ishihata_tech.hamiot_client.usecase

import android.util.Log
import com.ishihata_tech.hamiot_client.repo.FirebaseFunctionsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class GetDisplayNameImpl @Inject constructor(
    private val firebaseFunctionsRepository: FirebaseFunctionsRepository,
) : GetDisplayName {
    companion object {
        private const val TAG = "GetDisplayNameImpl"
    }

    /**
     * キャッシュ
     */
    private val cache = mutableMapOf<String, String>()

    override suspend fun invoke(accountId: String): String? {
        cache[accountId]?.also {
            return it
        }
        return suspendCoroutine { coroutine ->
            firebaseFunctionsRepository.functions
                .getHttpsCallable("getPublicUserData")
                .call(hashMapOf(
                    "accountId" to accountId
                ))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        (task.result?.data as? Map<*, *>)?.also { data ->
                            val resultCode = data["result"]
                            if (resultCode == "OK") {
                                val displayName = (data["displayName"] as? String) ?: ""
                                if (displayName.isNotEmpty()) {
                                    cache[accountId] = displayName
                                    coroutine.resume(displayName)
                                    return@addOnCompleteListener
                                }
                            } else {
                                Log.d(TAG, "onComplete: data=$data")
                            }
                        }
                    } else {
                        Log.d(TAG, "onComplete: failure: ${task.exception}")
                    }
                    // 失敗
                    coroutine.resume(null)
                }
        }
    }
}