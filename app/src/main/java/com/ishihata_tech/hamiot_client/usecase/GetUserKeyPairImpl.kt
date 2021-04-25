package com.ishihata_tech.hamiot_client.usecase

import com.ishihata_tech.hamiot_client.repo.UserKeyPairRepository
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3
import org.spongycastle.util.encoders.Hex
import java.security.KeyPair
import javax.inject.Inject

class GetUserKeyPairImpl @Inject constructor(
    private val userKeyPairRepository: UserKeyPairRepository,
) : GetUserKeyPair {
    override fun invoke(): KeyPair? {
        val publicKey = userKeyPairRepository.publicKey ?: return null
        val privateKey = userKeyPairRepository.privateKey ?: return null
        return Ed25519Sha3.keyPairFromBytes(Hex.decode(privateKey), Hex.decode(publicKey))
    }
}