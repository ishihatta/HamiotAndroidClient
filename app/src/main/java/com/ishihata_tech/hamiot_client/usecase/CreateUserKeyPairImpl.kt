package com.ishihata_tech.hamiot_client.usecase

import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3
import java.security.KeyPair
import javax.inject.Inject

class CreateUserKeyPairImpl @Inject constructor() : CreateUserKeyPair {
    override fun invoke(): KeyPair {
        return Ed25519Sha3().generateKeypair()
    }
}