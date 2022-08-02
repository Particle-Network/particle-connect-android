package com.connect.demo.utils

import com.connect.common.model.ChainType
import com.connect.common.model.EIP1559TransactionData
import com.connect.common.model.ITransactionData
import com.connect.common.provider.NetworkProvider
import com.connect.common.utils.Base58Utils
import com.connect.common.utils.HexUtils
import com.connect.demo.model.RpcRequest
import com.connect.demo.model.SOLTransfer
import com.connect.demo.model.WalletAccount
import com.connect.demo.transaction.SolanaRpcRepository
import com.connect.demo.transaction.SolanaTransactionManager
import com.particle.connect.ParticleConnect
import org.p2p.solanaj.core.Transaction
import java.math.BigInteger
import java.util.*


object MockManger {

    private val solanaRpcApi: SolanaRpcApi =
        NetworkProvider.createRetrofit("https://api.particle.network/solana/rpc/")
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
            mockCreateSendSplTokenTransaction(from)
        } else {
            return EIP1559TransactionData(
                "0x${ParticleConnect.chainId.toString(16)}",
                from,
                "0xAC6d81182998EA5c196a4424EA6AB250C7eb175b",
                "0x38d7ea4c68000",
                gasLimit = "0x${Integer.toHexString(25000)}",
                maxFeePerGas = "0x9502f90e",
                maxPriorityFeePerGas = "0x9502F900",
            )
        }
    }

    private suspend fun mockSendSolanaTransaction(from: String): Transaction {
        return SolanaTransactionManager.transferNativeToken(
            from,
            "DtbnGhuAzK1NhbgdBhX6DLLFzFJFFEpjtzrbxwVEZEAs",
            BigInteger("100000000")
        )
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
            BigInteger("100000000")
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