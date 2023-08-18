package com.connect.demo.transaction

import com.connect.common.provider.NetworkProvider
import com.connect.demo.model.RpcRequest
import com.connect.demo.model.TransactionAddressData
import com.connect.demo.utils.SolanaRpcApi
import com.particle.connect.ParticleConnect
import org.p2p.solanaj.core.PublicKey
import org.p2p.solanaj.kits.TokenTransaction
import org.p2p.solanaj.model.types.*
import org.p2p.solanaj.programs.TokenProgram
import java.util.*

/**
 * Created by chaichuanfa on 2022/7/29
 */
object SolanaRpcRepository {

    private val solanaRpcApi: SolanaRpcApi =
        NetworkProvider.createRetrofit("${com.connect.common.BuildConfig.PN_API_BASE_URL}/solana/")
            .create(SolanaRpcApi::class.java)

    suspend fun getRecentBlockhash(commitment: String = "finalized"): String {
        val response = solanaRpcApi.getRecentBlockhash(
            RpcRequest(
                ParticleConnect.chainId,
                UUID.randomUUID().toString(),
                method = "getRecentBlockhash",
                params = listOf(ConfigObjects.Commitment(commitment))
            )
        )
        return response.result.recentBlockhash
    }

    suspend fun findSplTokenAddressData(
        destinationAddress: PublicKey,
        mintAddress: String,
    ): TransactionAddressData {
        val associatedAddress = try {
            findSplTokenAddress(destinationAddress, mintAddress)
        } catch (e: IllegalStateException) {
            throw IllegalStateException("Invalid owner address")
        }

        /* If account is not found, create one */
        val accountInfo = getAccountInfo(associatedAddress.toBase58())
        val value = accountInfo?.value
        val accountExists = value?.owner == TokenProgram.PROGRAM_ID.toString() && value.data != null
        return TransactionAddressData(
            associatedAddress,
            !accountExists
        )
    }

    suspend fun getAccountInfo(account: String): AccountInfo? {
        val response = solanaRpcApi.getAccountInfo(
            RpcRequest(
                ParticleConnect.chainId,
                UUID.randomUUID().toString(),
                method = "getAccountInfo",
                params = listOf(
                    account,
                    RequestConfiguration(encoding = Encoding.BASE64.encoding)
                )
            )
        )
        return response.result
    }

    suspend fun findSplTokenAddress(
        destinationAddress: PublicKey,
        mintAddress: String
    ): PublicKey {
        val accountInfo = getAccountInfo(destinationAddress.toBase58())
        val info = TokenTransaction.parseAccountInfoData(accountInfo, TokenProgram.PROGRAM_ID)

        // create associated token address
        val value = accountInfo?.value
        if (value == null || value.data?.get(0).isNullOrEmpty()) {
            return TokenTransaction.getAssociatedTokenAddress(
                PublicKey(mintAddress),
                destinationAddress
            )
        }

        // detect if destination address is already a SPLToken address
        if (info?.mint == destinationAddress) {
            return destinationAddress
        }

        // detect if destination address is a SOL address
        if (info?.owner?.toBase58() == TokenProgram.PROGRAM_ID.toBase58()) {
            // create associated token address
            return TokenTransaction.getAssociatedTokenAddress(
                PublicKey(mintAddress),
                destinationAddress
            )
        }

        throw IllegalStateException("Wallet address is not valid")
    }

    suspend fun getTokenAccountsByOwner(owner: String): TokenAccounts {
        val programId = TokenProgram.PROGRAM_ID
        val programIdParam = HashMap<String, String>()
        programIdParam["programId"] = programId.toBase58()

        val encoding = HashMap<String, String>()
        encoding["encoding"] = "jsonParsed"
        encoding["commitment"] = "confirmed"

        val params = listOf(
            owner,
            programIdParam,
            encoding
        )

        val rpcRequest = RpcRequest(
            ParticleConnect.chainId,
            UUID.randomUUID().toString(),
            method = "getTokenAccountsByOwner",
            params = params,
        )
        return solanaRpcApi.getTokenAccountsByOwner(rpcRequest).result
    }

}