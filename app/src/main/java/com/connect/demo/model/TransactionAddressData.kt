package com.connect.demo.model

import org.p2p.solanaj.core.PublicKey

data class TransactionAddressData(
    val associatedAddress: PublicKey,
    val shouldCreateAssociatedInstruction: Boolean
)