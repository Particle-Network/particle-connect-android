package com.connect.demo.custom_connectadapter

import com.connect.common.ConnectManager
import com.connect.common.model.IconUrl
import com.connect.common.model.WalletName
import com.connect.common.model.WalletReadyState
import com.connect.common.model.WebsiteUrl
import com.connect.common.utils.AppUtils
import com.wallet.connect.adapter.BaseWalletConnectAdapter
import com.wallet.connect.adapter.model.MobileWCWallet


class Coin98ConnectAdapter : BaseWalletConnectAdapter() {

    val coin98 = MobileWCWallet(name = "Coin98", packageName = "coin98.crypto.finance.media", scheme = "coin98")

    override val name: WalletName = coin98.name

    override val icon: IconUrl = "https://registry.walletconnect.com/v2/logo/md/dee547be-936a-4c92-9e3f-7a2350a62e00"

    override val url: WebsiteUrl = "https://coin98.com"

    override val mobileWallet: MobileWCWallet = coin98

    override val readyState: WalletReadyState
        get() {
            if (supportChains.contains(ConnectManager.chainType)) {
                return if (AppUtils.isAppInstalled(
                        ConnectManager.context, coin98.packageName
                    )
                ) WalletReadyState.Installed else WalletReadyState.NotDetected
            }
            return WalletReadyState.Unsupported
        }
}