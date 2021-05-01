package com.ishihata_tech.hamiot_client.usecase

import android.util.Log
import com.ishihata_tech.hamiot_client.Constants.ASSET_ID
import com.ishihata_tech.hamiot_client.repo.FirebaseFunctionsRepository
import com.ishihata_tech.hamiot_client.repo.UserAccountRepository
import jp.co.soramitsu.iroha.java.Transaction
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TransferAssetOnServerImpl @Inject constructor(
        private val userAccountRepository: UserAccountRepository,
        private val firebaseFunctionsRepository: FirebaseFunctionsRepository,
) : TransferAssetOnServer {
    companion object {
        private const val TAG = "TransferAssetOnServerImpl"
    }

    override suspend fun invoke(opponentAccountId: String, amount: Int): Boolean = suspendCoroutine { coroutine ->
        // トランザクション作成
        val tx = Transaction.builder(userAccountRepository.accountId)
                .transferAsset(userAccountRepository.accountId, opponentAccountId, ASSET_ID, "", amount.toString())
                .sign(userAccountRepository.keyPair)
                .build()
        // バイナリに変換
        val txBinary = tx.toByteArray()
        // BASE64に変換
        val txBase64 = Base64.getEncoder().encodeToString(txBinary)

        firebaseFunctionsRepository.functions
                .getHttpsCallable("transferAsset")
                .call(hashMapOf(
                        "transaction" to txBase64
                ))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        Log.d(TAG, "onComplete: ${task.result?.data}")
                        (task.result?.data as? Map<*, *>)?.also { data ->
                            val resultCode = data["result"]
                            if (resultCode == "OK") {
                                coroutine.resume(true)
                                return@addOnCompleteListener
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