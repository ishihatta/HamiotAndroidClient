package com.ishihata_tech.hamiot_client.usecase

interface CreateNewAccount {
    suspend fun invoke(publicKey: String, displayName: String): Boolean
}