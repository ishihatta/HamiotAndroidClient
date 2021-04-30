package com.ishihata_tech.hamiot_client.usecase

/**
 * FCMトークンを取得する
 */
interface GetFcmToken {
    /**
     * FCMトークンを取得する
     *
     * @return FCMトークン。取得に失敗した場合null
     */
    suspend fun invoke(): String?
}