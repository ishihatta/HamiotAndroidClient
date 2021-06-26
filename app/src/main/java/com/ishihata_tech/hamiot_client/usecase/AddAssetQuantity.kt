package com.ishihata_tech.hamiot_client.usecase

/**
 * 現在のユーザの残高を増やす
 */
interface AddAssetQuantity {
    /**
     * 現在のユーザの残高を増やす
     *
     * @param ammount 増やす額
     * @return 成功したらtrue, 失敗したらfalse
     */
    suspend fun invoke(ammount: Int): Boolean
}