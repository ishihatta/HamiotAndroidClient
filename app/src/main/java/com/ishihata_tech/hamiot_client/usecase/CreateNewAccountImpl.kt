package com.ishihata_tech.hamiot_client.usecase

import android.util.Log
import com.ishihata_tech.hamiot_client.repo.FirebaseFunctionsRepository
import com.ishihata_tech.hamiot_client.repo.UserAccountRepository
import jp.co.soramitsu.iroha.java.Transaction
import jp.co.soramitsu.iroha.java.TransactionStatusObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    override suspend fun invoke(displayName: String, fcmToken: String): Boolean {
        val accountIdAndIrohaAddress = createAccount()
        if (accountIdAndIrohaAddress == null) {
            userAccountRepository.clear()
            return false
        }
        val accountId = accountIdAndIrohaAddress.first
        val irohaAddress = accountIdAndIrohaAddress.second

        userAccountRepository.accountId = accountId
        userAccountRepository.irohaAddress = irohaAddress

        if (!setAccountDetails(displayName, fcmToken)) {
            userAccountRepository.clear()
            return false
        }

        return true
    }

    private suspend fun createAccount(): Pair<String, String>? = suspendCoroutine { coroutine ->
        firebaseFunctionsRepository.functions
                .getHttpsCallable("newAccount")
                .call(hashMapOf(
                        "publicKey" to userAccountRepository.publicKey
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
                                    coroutine.resume(Pair(accountId, irohaAddress))
                                    return@addOnCompleteListener
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "onComplete: failure: ${task.exception}")
                    }
                    // 失敗
                    coroutine.resume(null)
                }
    }

    private suspend fun setAccountDetails(displayName: String, fcmToken: String): Boolean = withContext(Dispatchers.IO) {
        val irohaAPI = userAccountRepository.irohaApi
        if (irohaAPI != null) {
            var success = false

            // トランザクション作成
            val tx = Transaction.builder(userAccountRepository.accountId)
                    .setAccountDetail(userAccountRepository.accountId, "displayName", displayName)
                    .setAccountDetail(userAccountRepository.accountId, "fcmToken", fcmToken)
                    .sign(userAccountRepository.keyPair)
                    .build()

            // オブザーバ作成
            val observer = TransactionStatusObserver.builder()
                    .onTransactionFailed {
                        Log.d(TAG, "Transaction failed: $it")
                    }
                    .onError {
                        Log.d(TAG, "Failure with exception", it)
                    }
                    .onTransactionCommitted {
                        success = true
                    }
                    .build()

            // トランザクション実行
            irohaAPI.transaction(tx)
                    .blockingSubscribe(observer)

            success
        } else {
            false
        }
    }
}