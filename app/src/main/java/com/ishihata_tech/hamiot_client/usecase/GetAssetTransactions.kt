package com.ishihata_tech.hamiot_client.usecase

/**
 * 送金トランザクションのリストを取得する
 */
interface GetAssetTransactions {
    /**
     * トランザクションデータ
     */
    data class Transaction (
            val isReceived: Boolean,
            val opponentAccountId: String,
            val amount: Int,
            val time: Long,
    )

    /**
     * 結果
     */
    data class Result (
            val transactionList: List<Transaction>,
            val nextHash: String?,
    )

    /**
     * 送金トランザクションのリストを取得する
     *
     * @param firstHash ページングする際の最初のトランザクションのハッシュ。nullの場合は最初から取ってくる
     * @return 取得したデータ。失敗した場合はnull
     */
    suspend fun invoke(firstHash: String?): Result?
}