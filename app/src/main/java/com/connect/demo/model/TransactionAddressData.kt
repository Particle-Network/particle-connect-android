package com.connect.demo.model

import org.p2p.solanaj.core.PublicKey

/**
 * Created by chaichuanfa on 2022/7/28
 */
data class TransactionAddressData(
    val associatedAddress: PublicKey,
    val shouldCreateAssociatedInstruction: Boolean
)