package com.connect.demo.model

import com.connect.common.IConnectAdapter
import com.connect.common.model.Account


data class WalletAccount(
    val name: String,
    val account: Account,
    val connectAdapter: IConnectAdapter,
)