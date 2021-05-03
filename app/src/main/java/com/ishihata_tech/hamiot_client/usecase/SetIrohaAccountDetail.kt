package com.ishihata_tech.hamiot_client.usecase

/**
 * Irohaアカウントにkey-valueのデータをセットする
 */
interface SetIrohaAccountDetail {
    /**
     * Irohaアカウントにkey-valueのデータをセットする
     *
     * @param key キー
     * @param value セットする値
     * @return 成功したらtrue, 失敗したらfalse
     */
    suspend fun invoke(key: String, value: String): Boolean
}