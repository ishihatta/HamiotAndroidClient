package com.ishihata_tech.hamiot_client.usecase

/**
 * 現在のユーザの残高を取得する
 */
interface GetBalance {
    /**
     * 現在のユーザの残高を取得する
     *
     * @return 残高。取得に失敗した場合はnull
     */
    suspend fun invoke(): Int?
}