package com.ishihata_tech.hamiot_client.usecase

import android.util.Log
import com.ishihata_tech.hamiot_client.Constants.ASSET_ID
import com.ishihata_tech.hamiot_client.repo.UserAccountRepository
import jp.co.soramitsu.iroha.java.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetBalanceImpl @Inject constructor(
        private val userAccountRepository: UserAccountRepository,
) : GetBalance {
    companion object {
        private const val TAG = "GetBalanceImpl"
    }

    override suspend fun invoke(): Int? = withContext(Dispatchers.IO) {
        val irohaAPI = userAccountRepository.irohaApi
        if (irohaAPI != null) {
            val query = Query.builder(userAccountRepository.accountId, 1)
                    .getAccountAssets(userAccountRepository.accountId, 1, ASSET_ID)
                    .buildSigned(userAccountRepository.keyPair)

            val response = try {
                irohaAPI.query(query)
            } catch (e: Exception) {
                Log.d(TAG, "Failure to query balance", e)
                null
            }
            val assets = response?.accountAssetsResponse?.accountAssetsList
            if (assets == null) {
                null
            } else {
                assets.find { it.assetId == ASSET_ID }?.balance?.toInt() ?: 0
            }
        } else {
            Log.d(TAG, "irohaAPI is null")
            null
        }
    }
}