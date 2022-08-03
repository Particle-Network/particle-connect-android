package com.connect.demo.model

/**
 * Created by chaichuanfa on 2022/7/27
 */
data class RpcRequest(
    val chainId: Int,
    val id: String,
    val jsonrpc: String = "2.0",
    val method: String,
    val params: List<Any>? = null,
)
