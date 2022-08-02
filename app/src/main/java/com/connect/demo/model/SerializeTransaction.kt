package com.connect.demo.model


data class SerializeTransaction(
    val transaction: Transaction
)

data class Transaction(
    val hasPartialSign: Boolean,
    val serialized: String
)

data class SOLTransfer(
    val sender: String,
    val receiver: String,
    val lamports: Long,
)