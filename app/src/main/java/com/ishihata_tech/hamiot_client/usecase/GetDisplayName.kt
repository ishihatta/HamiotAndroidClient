package com.ishihata_tech.hamiot_client.usecase

/**
 * サーバからアカウントの表示名を取得する
 */
interface GetDisplayName {
    /**
     * サーバからアカウントの表示名を取得する
     *
     * @param accountId IrohaのアカウントID
     * @return 表示名。取得に失敗した場合null
     */
    suspend fun invoke(accountId: String): String?
}