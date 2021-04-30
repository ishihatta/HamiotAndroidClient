package com.ishihata_tech.hamiot_client.usecase

import com.ishihata_tech.hamiot_client.repo.UserAccountRepository
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3
import org.spongycastle.util.encoders.Hex
import java.security.KeyPair
import javax.inject.Inject

class GetUserKeyPairImpl @Inject constructor(
    private val userAccountRepository: UserAccountRepository,
) : GetUserKeyPair {
    override fun invoke(): KeyPair? {
        return userAccountRepository.keyPair
    }
}