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
     * @return 成功したらtrue, 失敗したらfalse
     */
    fun invoke(uri: Uri): Boolean
}