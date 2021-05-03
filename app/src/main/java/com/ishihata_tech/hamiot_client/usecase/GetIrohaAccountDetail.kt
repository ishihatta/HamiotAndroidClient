package com.ishihata_tech.hamiot_client.usecase

/**
 * Irohaアカウントのkey-valueのデータを取得する
 */
interface GetIrohaAccountDetail {
    /**
     * Irohaアカウントのkey-valueのデータを取得する
     *
     * @param key キー
     * @return セットされている値, 取得に失敗したらnull
     */
    suspend fun invoke(key: String): String?
}