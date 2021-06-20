package com.ishihata_tech.hamiot_client.usecase

import android.net.Uri

/**
 * アカウント情報をバックアップファイルに出力する
 */
interface BackupAccount {
    /**
     * アカウント情報をバックアップファイルに出力する
     *
     * @param uri 出力先
     * @param password 秘密鍵を暗号化するためのパスワード
     * @return 成功したらtrue, 失敗したらfalse
     */
    fun invoke(uri: Uri, password: String): Boolean
}