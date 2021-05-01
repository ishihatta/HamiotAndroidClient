package com.ishihata_tech.hamiot_client.usecase

/**
 * 送金する
 */
interface TransferAssetOnServer {
    /**
     * 送金する
     *
     * @param opponentAccountId 送金先のアカウントID
     * @param amount 送金額
     * @return 成功したらtrue, 失敗したらfalse
     */
    suspend fun invoke(opponentAccountId: String, amount: Int): Boolean
}