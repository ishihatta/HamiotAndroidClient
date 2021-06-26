package com.ishihata_tech.hamiot_client.usecase

/**
 * 現在のユーザのロール一覧を取得する
 */
interface GetOwnAccountRoles {
    /**
     * 現在のロールの一覧を取得する
     *
     * @return ロールの一覧
     */
    suspend fun invoke(): List<String>?
}