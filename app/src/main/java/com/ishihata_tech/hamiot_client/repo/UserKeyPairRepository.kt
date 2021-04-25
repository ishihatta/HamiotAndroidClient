package com.ishihata_tech.hamiot_client.repo

/**
 * ユーザのキーペアをローカルに保存するリポジトリ
 */
interface UserKeyPairRepository {
    /**
     * 公開鍵
     */
    var publicKey: String?

    /**
     * 秘密鍵
     */
    var privateKey: String?

    /**
     * 保存しているキーペア情報をすべて削除する
     */
    fun clear()
}