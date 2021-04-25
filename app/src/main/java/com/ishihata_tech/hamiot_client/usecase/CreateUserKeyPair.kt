package com.ishihata_tech.hamiot_client.usecase

import java.security.KeyPair

/**
 * ユーザの新しいキーペアを生成する
 */
interface CreateUserKeyPair {
    /**
     * ユーザの新しいキーペアを生成する
     *
     * @return 生成されたキーペア
     */
    fun invoke(): KeyPair
}