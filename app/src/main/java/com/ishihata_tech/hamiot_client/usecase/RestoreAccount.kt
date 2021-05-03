package com.ishihata_tech.hamiot_client.usecase

import android.net.Uri

/**
 * バックアップファイルからアカウント情報をリストアする
 */
interface RestoreAccount {
    /**
     * バックアップファイルからアカウント情報をリストアする
     *
     * @param uri バックアップ元
     * @return 成功したらtrue, 失敗したらfalse
     */
    fun invoke(uri: Uri): Boolean
}