package com.ishihata_tech.hamiot_client.util

import org.junit.Test

import org.junit.Assert.*
import org.spongycastle.util.encoders.Hex

class KeyEncryptorTest {
    @Test
    fun encryptAndDecrypt_isCorrect() {
        val key = "0123456789abcdef0123456789ABCDEF".toByteArray()
        val password = "password"
        val encryptedKey = KeyEncryptor.encrypt(key, password)
        assertNotNull(encryptedKey)
        val decryptedKey = KeyEncryptor.decrypt(encryptedKey!!, password)
        assertNotNull(decryptedKey)
        assertEquals(Hex.toHexString(key), Hex.toHexString(decryptedKey))
    }
}