package com.ishihata_tech.hamiot_client.usecase

import java.security.KeyPair

/**
 * ユーザのキーペアをローカルから取得する
 */
interface GetUserKeyPair {
    /**
     * ユーザのキーペアをローカルから取得する
     *
     * @return ユーザのキーペア。保存されていない場合は null
     */
    fun invoke(): KeyPair?
}