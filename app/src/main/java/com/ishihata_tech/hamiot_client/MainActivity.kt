package com.ishihata_tech.hamiot_client

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3
import jp.co.soramitsu.iroha.java.IrohaAPI
import jp.co.soramitsu.iroha.java.Query
import jp.co.soramitsu.iroha.java.Utils
import org.spongycastle.util.encoders.Hex


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val adminPubKey = "313a07e6384776ed95447710d15e59148473ccfc052a681317a72a69f2a49910"
    private val adminPrivKey = "f101537e319568c765b2cc89698325604991dca57b9716b58016b253506cab70"
    private val adminKeyPair =
        Ed25519Sha3.keyPairFromBytes(Hex.decode(adminPrivKey), Hex.decode(adminPubKey))
    private val irohaAPI = IrohaAPI("localhost", 50051)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    private fun printBlock(height: Long) {
        val query = Query.builder("admin@test", 1).getBlock(height).buildSigned(adminKeyPair)
        val response = irohaAPI.query(query).blockResponse

        // ハッシュの計算
        val blockV1 = response.block.blockV1
        val hash = Utils.hash(blockV1)
        println("hash:" + Hex.toHexString(hash))

        // 署名の検証
        for (sig in blockV1.signaturesList) {
            val publicKey = Utils.parseHexPublicKey(sig.publicKey)
            val signature = Hex.decode(sig.signature)
            val verifyOk = Ed25519Sha3().rawVerify(hash, signature, publicKey)
            println("verify:$verifyOk")
        }

        // ブロックの内容を出力
        println(response)
    }
}