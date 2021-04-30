package com.ishihata_tech.hamiot_client.repo

import jp.co.soramitsu.iroha.java.IrohaAPI
import java.security.KeyPair

/**
 * ローカルに保存するユーザアカウント情報のリポジトリ
 */
interface UserAccountRepository {
    /**
     * 公開鍵
     */
    var publicKey: String?

    /**
     * 秘密鍵
     */
    var privateKey: String?

    /**
     * アカウントID
     */
    var accountId: String?

    /**
     * 表示名
     */
    var displayName: String?

    /**
     * Iroha ノードのアドレス（ホスト名:ポート番号）
     */
    var irohaAddress: String?

    /**
     * Iroha API
     */
    val irohaApi: IrohaAPI?

    /**
     * キーペア
     */
    val keyPair: KeyPair?

    /**
     * 保存しているキーペア情報をすべて削除する
     */
    fun clear()
}