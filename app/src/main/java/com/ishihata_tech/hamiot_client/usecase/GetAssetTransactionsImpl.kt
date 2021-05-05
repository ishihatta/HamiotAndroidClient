package com.ishihata_tech.hamiot_client.usecase

import android.util.Log
import com.ishihata_tech.hamiot_client.Constants
import com.ishihata_tech.hamiot_client.repo.UserAccountRepository
import iroha.protocol.Queries
import jp.co.soramitsu.iroha.java.Query
import jp.co.soramitsu.iroha.java.QueryBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAssetTransactionsImpl @Inject constructor(
        private val userAccountRepository: UserAccountRepository,
) : GetAssetTransactions {
    companion object {
        private const val TAG = "GetAssetTransactionsImpl"
        private const val PAGE_SIZE = 30
    }

    override suspend fun invoke(firstHash: String?): GetAssetTransactions.Result? = withContext(Dispatchers.IO) {
        userAccountRepository.irohaApi?.let { irohaAPI ->
            // Create query
            val myAccountId = userAccountRepository.accountId
            val query = Query.builder(userAccountRepository.accountId, 1)
                    .getAccountAssetTransactions(
                        myAccountId,
                        Constants.ASSET_ID,
                        PAGE_SIZE, firstHash,
                        QueryBuilder.Ordering().apply {
                            addFieldOrdering(
                                Queries.Field.kCreatedTime,
                                Queries.Direction.kDescending
                            )
                        }
                    )
                    .buildSigned(userAccountRepository.keyPair)

            // Execute the query
            val response = try {
                irohaAPI.query(query)
            } catch (e: Exception) {
                Log.d(TAG, "Failure to query transactions", e)
                null
            }

            // Check response
            response?.transactionsPageResponse?.let { transactionsPageResponse ->
                val transactionList = mutableListOf<GetAssetTransactions.Transaction>()
                transactionsPageResponse.transactionsList?.forEach { transaction ->
                    //Log.d(TAG, "transaction=$transaction")
                    transaction.payload.reducedPayload.commandsList.forEach { command ->
                        if (command.hasTransferAsset()) {
                            val srcAccountId = command.transferAsset.srcAccountId
                            val destAccountId = command.transferAsset.destAccountId
                            val amount = command.transferAsset.amount.toInt()
                            val createdTime = transaction.payload.reducedPayload.createdTime
                            Log.d(TAG, "srcAccountId=$srcAccountId destAccountId=$destAccountId amount=$amount createdTime=$createdTime")

                            val isReceived = destAccountId == myAccountId
                            transactionList.add(GetAssetTransactions.Transaction(
                                    isReceived,
                                    if (isReceived) srcAccountId else destAccountId,
                                    amount,
                                    createdTime
                            ))
                        }
                    }
                }
                val nextTxHash = response.transactionsPageResponse?.nextTxHash
                Log.d(TAG, "nextTxHash=$nextTxHash")
                GetAssetTransactions.Result(transactionList, nextTxHash)
            }
        }
    }
}