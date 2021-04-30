package com.ishihata_tech.hamiot_client.usecase

import com.ishihata_tech.hamiot_client.repo.UserAccountRepository
import org.spongycastle.util.encoders.Hex
import java.security.KeyPair
import javax.inject.Inject

class StoreUserKeyPairImpl @Inject constructor(
    private val userAccountRepository: UserAccountRepository,
) : StoreUserKeyPair {
    override fun invoke(keyPair: KeyPair) {
        userAccountRepository.publicKey = Hex.toHexString(keyPair.public.encoded)
        userAccountRepository.privateKey = Hex.toHexString(keyPair.private.encoded)
    }
}