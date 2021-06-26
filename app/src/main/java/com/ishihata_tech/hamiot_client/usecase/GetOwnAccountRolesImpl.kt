package com.ishihata_tech.hamiot_client.usecase

import android.util.Log
import com.ishihata_tech.hamiot_client.repo.UserAccountRepository
import jp.co.soramitsu.iroha.java.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetOwnAccountRolesImpl @Inject constructor(
    private val userAccountRepository: UserAccountRepository,
) : GetOwnAccountRoles {
    companion object {
        private const val TAG = "GetOwnAccountImpl"
    }

    override suspend fun invoke(): List<String>? = withContext(Dispatchers.IO) {
        val irohaAPI = userAccountRepository.irohaApi
        if (irohaAPI != null) {
            val query = Query.builder(userAccountRepository.accountId, 1)
                .getAccount(userAccountRepository.accountId)
                .buildSigned(userAccountRepository.keyPair)

            val response = try {
                irohaAPI.query(query)
            } catch (e: Exception) {
                Log.d(TAG, "Failure to query balance", e)
                null
            }
            response?.accountResponse?.accountRolesList.also {
                it?.forEach { role -> Log.d(TAG, "role: $role") }
            }
        } else {
            Log.d(TAG, "irohaAPI is null")
            null
        }
    }
}