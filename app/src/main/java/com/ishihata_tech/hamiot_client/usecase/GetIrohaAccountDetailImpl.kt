package com.ishihata_tech.hamiot_client.usecase

import android.util.Log
import com.ishihata_tech.hamiot_client.repo.UserAccountRepository
import jp.co.soramitsu.iroha.java.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetIrohaAccountDetailImpl @Inject constructor(
        private val userAccountRepository: UserAccountRepository,
) : GetIrohaAccountDetail {
    companion object {
        private const val TAG = "GetIrohaAccountDetailImpl"
    }

    override suspend fun invoke(key: String): String? = withContext(Dispatchers.IO) {
        val irohaAPI = userAccountRepository.irohaApi
        if (irohaAPI != null) {
            val query = Query.builder(userAccountRepository.accountId, 1)
                    .getAccountDetail(
                            userAccountRepository.accountId,
                            userAccountRepository.accountId,
                            key,
                            1,
                            userAccountRepository.accountId,
                            key
                    )
                    .buildSigned(userAccountRepository.keyPair)

            val response = try {
                irohaAPI.query(query)
            } catch (e: Exception) {
                Log.d(TAG, "Failure to query balance", e)
                null
            }
            val detail = response?.accountDetailResponse?.detail
            detail
        } else {
            Log.d(TAG, "irohaAPI is null")
            null
        }
    }

}