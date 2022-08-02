package com.connect.demo.utils

import com.connect.demo.model.RpcRequest
import com.connect.demo.model.RpcResponse
import com.connect.demo.model.SerializeTransaction
import org.p2p.solanaj.model.types.AccountInfo
import org.p2p.solanaj.model.types.FeesResponse
import org.p2p.solanaj.model.types.RecentBlockhash
import org.p2p.solanaj.model.types.TokenAccounts
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url


interface SolanaRpcApi {

    @POST
    suspend fun enhancedSerializeTransaction(
        @Body body: RpcRequest,
        @Url url: String = ""
    ): RpcResponse<SerializeTransaction>

    @POST
    suspend fun getRecentBlockhash(
        @Body body: RpcRequest,
        @Url url: String = ""
    ): RpcResponse<RecentBlockhash>

    @POST
    suspend fun getAccountInfo(
        @Body body: RpcRequest,
        @Url url: String = ""
    ): RpcResponse<AccountInfo?>

    @POST
    suspend fun getMinimumBalanceForRentExemption(
        @Body rpcRequest: RpcRequest,
        @Url url: String = ""
    ): RpcResponse<Long>

    @POST
    suspend fun getFees(
        @Body rpcRequest: RpcRequest,
        @Url url: String = ""
    ): RpcResponse<FeesResponse>

    @POST
    suspend fun getTokenAccountsByOwner(
        @Body rpcRequest: RpcRequest,
        @Url url: String = ""
    ): RpcResponse<TokenAccounts>
}