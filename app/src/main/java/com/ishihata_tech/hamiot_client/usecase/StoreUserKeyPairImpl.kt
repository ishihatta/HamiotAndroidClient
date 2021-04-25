package com.ishihata_tech.hamiot_client.usecase

import com.ishihata_tech.hamiot_client.repo.UserKeyPairRepository
import org.spongycastle.util.encoders.Hex
import java.security.KeyPair
import javax.inject.Inject

class StoreUserKeyPairImpl @Inject constructor(
    private val userKeyPairRepository: UserKeyPairRepository,
) : StoreUserKeyPair {
    override fun invoke(keyPair: KeyPair) {
        userKeyPairRepository.publicKey = Hex.toHexString(keyPair.public.encoded)
        userKeyPairRepository.privateKey = Hex.toHexString(keyPair.private.encoded)
    }
}