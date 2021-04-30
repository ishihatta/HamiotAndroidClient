package com.ishihata_tech.hamiot_client.repo

import com.google.firebase.functions.FirebaseFunctions

/**
 * Firebase functions のリポジトリ
 */
interface FirebaseFunctionsRepository {
    /**
     * Firebase functions のインスタンス
     */
    val functions: FirebaseFunctions
}