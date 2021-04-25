package com.ishihata_tech.hamiot_client.usecase

import java.security.KeyPair

/**
 * ユーザのキーペアをローカルに保存する
 */
interface StoreUserKeyPair {
    /**
     * ユーザのキーペアをローカルに保存する
     *
     * @param keyPair 保存するキーペア
     */
    fun invoke(keyPair: KeyPair)
}