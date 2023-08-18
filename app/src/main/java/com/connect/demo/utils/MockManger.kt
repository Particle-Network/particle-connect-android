package com.connect.demo.utils

import com.connect.common.ConnectManager
import com.connect.common.provider.NetworkProvider
import com.connect.demo.model.RpcRequest
import com.connect.demo.model.SOLTransfer
import com.connect.demo.model.WalletAccount
import com.connect.demo.transaction.SolanaTransactionManager
import com.particle.base.model.ChainType
import com.particle.base.model.EIP1559TransactionData
import com.particle.base.model.LegacyTransactionData
import com.particle.base.utils.Base58Utils
import com.particle.base.utils.HexUtils
import com.particle.connect.ParticleConnect
import org.p2p.solanaj.core.ITransactionData
import org.p2p.solanaj.core.Transaction
import java.math.BigInteger
import java.util.UUID

/**
 * Created by chaichuanfa on 2022/7/26
 */
object MockManger {

    private val solanaRpcApi: SolanaRpcApi =
        NetworkProvider.createRetrofit("${com.connect.common.BuildConfig.PN_API_BASE_URL}")
            .create(SolanaRpcApi::class.java)

    private var minBalanceForRentExemption: BigInteger? = null
    private var lamportsPerSignature: BigInteger? = null

    var walletAccount: WalletAccount? = null


    fun encode(message: String): String {
        return if (ParticleConnect.chainType == ChainType.Solana) {
            Base58Utils.encode(message.toByteArray(Charsets.UTF_8))
        } else {
            HexUtils.encodeWithPrefix(message.toByteArray(Charsets.UTF_8))
        }
    }

    suspend fun mockCreateTransaction(from: String): ITransactionData {
        return if (ParticleConnect.chainType == ChainType.Solana) {
            mockSendSolanaTransaction(from)
        } else {
            if (ConnectManager.chainInfo.isEIP1559Supported()) {
                EIP1559TransactionData(
                    "0x${ParticleConnect.chainId.toString(16)}",
                    from,
                    "0x504F83D65029fB607fcAa43ebD0b7022ab161B0C",
                    "0x9184e72a000",
                    gasLimit = "0x${Integer.toHexString(25000)}",
                    maxFeePerGas = "0x9502f90e",
                    maxPriorityFeePerGas = "0x9502F900",
                )
            } else {
                LegacyTransactionData(
                    "0x${ParticleConnect.chainId.toString(16)}",
                    from = from,
                    to = "0x504F83D65029fB607fcAa43ebD0b7022ab161B0C",
                    value = "0x2386f26fc10000",
                    data = "0x",
                    nonce = "0x0",
                    gasPrice = "0x25cfcb580",
                    gasLimit = "0x5208",
                    type = "0x0",
                    action = "normal",
                    gasLevel = "medium"
                )
            }

        }
    }

    private suspend fun mockSendSolanaTransaction(from: String): Transaction {
        val tx = SolanaTransactionManager.transferNativeToken(
            from,
            "DtbnGhuAzK1NhbgdBhX6DLLFzFJFFEpjtzrbxwVEZEAs",
            BigInteger("100000"),
        )

        return tx
    }

    private suspend fun mockCreateSendSplTokenTransaction(
        from: String,
    ): Transaction {
        val tokenMintAddress = "GobzzzFQsFAHPvmwT42rLockfUCeV3iutEkK218BxT8K"
        val destinationAddress = "DtbnGhuAzK1NhbgdBhX6DLLFzFJFFEpjtzrbxwVEZEAs"

        return SolanaTransactionManager.transferSplToken(
            from,
            destinationAddress,
            tokenMintAddress,
            BigInteger("100000")
        )
    }

    suspend fun createSolanaTransaction(
        sender: String,
        receiver: String,
        lamports: Long,
    ): String {
        val response = solanaRpcApi.enhancedSerializeTransaction(
            RpcRequest(
                ParticleConnect.chainId,
                UUID.randomUUID().toString(),
                method = "enhancedSerializeTransaction",
                params = listOf("transfer-sol", SOLTransfer(sender, receiver, lamports))
            )
        )
        return response.result.transaction.serialized
    }

}