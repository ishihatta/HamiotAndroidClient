package com.ishihata_tech.hamiot_client.util

import java.io.ByteArrayOutputStream
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * キー情報を暗号化/復号するオブジェクト
 */
object KeyEncryptor {
    /**
     * 暗号化する
     *
     * @param key 暗号化対象のキー
     * @param password パスワード
     * @return 暗号化されたキー
     */
    fun encrypt(key: ByteArray, password: String): ByteArray? {
        return try {
            val pass = SecretKeySpec(normalizePassword(password), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, pass)
            val outputStream = ByteArrayOutputStream(64)
            outputStream.write(cipher.iv)
            outputStream.write(cipher.doFinal(key))
            outputStream.toByteArray()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 復号する
     *
     * @param encryptedKey 暗号化されたキー
     * @param password パスワード
     * @return 復号されたキー
     */
    fun decrypt(encryptedKey: ByteArray, password: String): ByteArray? {
        val pass = SecretKeySpec(normalizePassword(password), "AES")
        val iv = encryptedKey.copyOfRange(0, 16)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, pass, ivSpec)
        return cipher.doFinal(encryptedKey.copyOfRange(16, encryptedKey.size))
    }

    /**
     * パスワードを16バイトのバイト配列に正規化する
     *
     * @param password 正規化対象のパスワード
     * @return 正規化されたパスワード
     */
    private fun normalizePassword(password: String): ByteArray {
        val default = "`ehV6)@Aul[Xg ;r".toByteArray()
        val base = password.toByteArray()
        return ByteArray(16).apply {
            for (i in 0 until 16) {
                this[i] = if (i < base.size) base[i] else default[i]
            }
        }
    }
}