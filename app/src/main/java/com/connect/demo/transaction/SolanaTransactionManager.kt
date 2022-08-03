package com.connect.demo.transaction

import org.p2p.solanaj.core.PublicKey
import org.p2p.solanaj.core.Transaction
import org.p2p.solanaj.core.TransactionInstruction
import org.p2p.solanaj.kits.TokenTransaction
import org.p2p.solanaj.programs.SystemProgram
import org.p2p.solanaj.programs.TokenProgram
import java.math.BigInteger

/**
 * Created by chaichuanfa on 2022/7/29
 */
object SolanaTransactionManager {

    suspend fun transferNativeToken(
        fromAddress: String,
        destinationAddress: String,
        lamports: BigInteger,
        recentBlockhash: String? = null,
        feePayerPublicKey: String? = null,
    ): Transaction {
        if (fromAddress == destinationAddress) {
            error("You can not send tokens to yourself")
        }

        val transaction = Transaction()
        transaction.addInstruction(
            SystemProgram.transfer(
                PublicKey(fromAddress),
                PublicKey(destinationAddress),
                lamports
            )
        )
        transaction.feePayer = PublicKey(feePayerPublicKey ?: fromAddress)
        transaction.recentBlockHash = recentBlockhash ?: SolanaRpcRepository.getRecentBlockhash()
        return transaction
    }

    suspend fun transferSplToken(
        fromAddress: String,
        destinationAddress: String,
        mintAddress: String,
        lamports: BigInteger,
        recentBlockhash: String? = null,
        feePayerAddress: String? = null,
    ): Transaction {
        val transaction = Transaction()
        val destinationPublicKey = PublicKey(destinationAddress)
        val feePayer = PublicKey(feePayerAddress ?: fromAddress)
        val senderPublicKey = PublicKey(fromAddress)

        val senderAta = TokenTransaction.getAssociatedTokenAddress(
            PublicKey(mintAddress),
            senderPublicKey
        )

        val splDestinationAddress = SolanaRpcRepository.findSplTokenAddressData(
            destinationAddress = destinationPublicKey,
            mintAddress = mintAddress
        )
        // get address
        val toPublicKey = splDestinationAddress.associatedAddress
        val instructions = mutableListOf<TransactionInstruction>()

        // create associated token address
        if (splDestinationAddress.shouldCreateAssociatedInstruction) {
            val createAccount = TokenProgram.createAssociatedTokenAccountInstruction(
                TokenProgram.ASSOCIATED_TOKEN_PROGRAM_ID,
                TokenProgram.PROGRAM_ID,
                PublicKey(mintAddress),
                toPublicKey,
                destinationPublicKey,
                feePayer
            )

            instructions += createAccount
        }

        instructions += TokenProgram.transferInstruction(
            TokenProgram.PROGRAM_ID,
            senderAta,
            toPublicKey,
            senderPublicKey,
            lamports
        )

        transaction.addInstructions(instructions)
        transaction.recentBlockHash = recentBlockhash ?: SolanaRpcRepository.getRecentBlockhash()
        transaction.feePayer = feePayer

        return transaction
    }
}