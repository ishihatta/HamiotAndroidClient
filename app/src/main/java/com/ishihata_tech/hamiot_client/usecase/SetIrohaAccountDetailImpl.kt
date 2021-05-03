package com.ishihata_tech.hamiot_client.usecase

import android.util.Log
import com.ishihata_tech.hamiot_client.repo.UserAccountRepository
import jp.co.soramitsu.iroha.java.Transaction
import jp.co.soramitsu.iroha.java.TransactionStatusObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SetIrohaAccountDetailImpl @Inject constructor(
        private val userAccountRepository: UserAccountRepository,
) : SetIrohaAccountDetail {
    companion object {
        private const val TAG = "SetIrohaAccountDetailImpl"
    }

    override suspend fun invoke(key: String, value: String): Boolean = withContext(Dispatchers.IO) {
        val irohaAPI = userAccountRepository.irohaApi
        if (irohaAPI != null) {
            var success = false

            // トランザクション作成
            val tx = Transaction.builder(userAccountRepository.accountId)
                    .setAccountDetail(userAccountRepository.accountId, key, value)
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