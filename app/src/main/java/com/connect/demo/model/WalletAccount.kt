package com.connect.demo.model

import com.connect.common.IConnectAdapter
import com.connect.common.model.Account

/**
 * Created by chaichuanfa on 2022/7/25
 */
data class WalletAccount(
    val name: String,
    val account: Account,
    val connectAdapter: IConnectAdapter,
)