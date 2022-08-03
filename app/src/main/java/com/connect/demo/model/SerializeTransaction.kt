package com.connect.demo.model

/**
 * Created by chaichuanfa on 2022/7/27
 */
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