package com.ishihata_tech.hamiot_client.repo

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3
import jp.co.soramitsu.iroha.java.IrohaAPI
import org.spongycastle.util.encoders.Hex
import java.security.KeyPair
import javax.inject.Singleton
import javax.inject.Inject

@Singleton
class UserAccountRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : UserAccountRepository {
    companion object {
        private const val SHARED_PREFERENCES_NAME = "UserAccountRepositoryImpl"
        private const val PUBLIC_KEY = "public_key"
        private const val PRIVATE_KEY = "private_key"
        private const val ACCOUNT_ID = "account_id"
        private const val DISPLAY_NAME = "display_name"
        private const val IROHA_ADDRESS = "iroha_address"
    }

    private val sharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    override var publicKey: String?
        get() = sharedPreferences.getString(PUBLIC_KEY, null)
        set(value) { sharedPreferences.edit { putString(PUBLIC_KEY, value) } }

    override var privateKey: String?
        get() = sharedPreferences.getString(PRIVATE_KEY, null)
        set(value) { sharedPreferences.edit { putString(PRIVATE_KEY, value) } }

    override var accountId: String?
        get() = sharedPreferences.getString(ACCOUNT_ID, null)
        set(value) { sharedPreferences.edit { putString(ACCOUNT_ID, value) } }

    override var displayName: String?
        get() = sharedPreferences.getString(DISPLAY_NAME, null)
        set(value) { sharedPreferences.edit { putString(DISPLAY_NAME, value) } }

    override var irohaAddress: String?
        get() = sharedPreferences.getString(IROHA_ADDRESS, null)
        set(value) { sharedPreferences.edit { putString(IROHA_ADDRESS, value) } }

    override val irohaApi: IrohaAPI?
        get() {
            val address = irohaAddress ?: return null
            val hostAndPort = address.split(':')
            return IrohaAPI(
                    "10.0.2.2",
                    //hostAndPort[0],
                    hostAndPort[1].toInt())
        }

    override val keyPair: KeyPair?
        get() {
            val pubKey = publicKey ?: return null
            val privKey = privateKey ?: return null
            return Ed25519Sha3.keyPairFromBytes(Hex.decode(privKey), Hex.decode(pubKey))
        }

    override fun clear() {
        sharedPreferences.edit { clear() }
    }
}