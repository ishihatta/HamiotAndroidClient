package com.ishihata_tech.hamiot_client.usecase

/**
 * 新規アカウントを作成する
 */
interface CreateNewAccount {
    /**
     * 新規アカウントを作成する
     *
     * @param displayName 表示名
     * @param fcmToken FCMトークン
     * @return 成功したらtrue, 失敗したらfalse
     */
    suspend fun invoke(displayName: String, fcmToken: String): Boolean
}