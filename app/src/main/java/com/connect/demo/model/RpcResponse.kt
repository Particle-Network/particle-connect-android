package com.connect.demo.model


data class RpcResponse<T>(
    val chainId: Int,
    val jsonrpc: String,
    val id: String,
    val result: T,
)
