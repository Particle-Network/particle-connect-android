package com.connect.demo.model

/**
 * Created by chaichuanfa on 2022/7/27
 */
data class RpcResponse<T>(
    val chainId: Int,
    val jsonrpc: String,
    val id: String,
    val result: T,
)
