package com.connect.demo

import android.app.Application
import com.connect.demo.utils.CoilLoader
import com.evm.adapter.EVMConnectAdapter
import com.particle.base.Env
import com.particle.base.model.DAppMetadata
import com.particle.connect.ParticleConnect
import com.phantom.adapter.PhantomConnectAdapter
import com.solana.adapter.SolanaConnectAdapter
import com.wallet.connect.adapter.BitKeepConnectAdapter
import com.wallet.connect.adapter.ImTokenConnectAdapter
import com.wallet.connect.adapter.MetaMaskConnectAdapter
import com.wallet.connect.adapter.ParticleWalletConnectAdapter
import com.wallet.connect.adapter.RainbowConnectAdapter
import com.wallet.connect.adapter.TrustConnectAdapter
import com.wallet.connect.adapter.WalletConnectAdapter
import com.wallet.connect.adapter.secondory.AlphaWalletConnectAdapter
import com.wallet.connect.adapter.secondory.BitpieConnectAdapter
import com.wallet.connect.adapter.secondory.Coin98ConnectAdapter
import com.wallet.connect.adapter.secondory.TokenPocketConnectAdapter
import network.particle.chains.ChainInfo.Companion.EthereumGoerli
import particle.auth.adapter.ParticleConnectAdapter

/**
 * Created by chaichuanfa on 2022/7/15
 */
class App : Application() {

    private lateinit var instance: App

    override fun onCreate() {
        super.onCreate()
        instance = this
        CoilLoader.init(this)
        ParticleConnect.init(
            this, Env.DEV, EthereumGoerli, DAppMetadata(
                walletConnectProjectId = "f431aaea6e4dea6a669c0496f9c009c1",
                name = "Particle Connect",
                icon = "https://connect.particle.network/icons/512.png",
                url = "https://particle.network",
                description = "Particle Connect is a decentralized wallet connection solution that allows users to connect to DApps with their wallets.",
                redirect = "redirect://",
                verifyUrl = "verifyUrl",
            )
        ) {
            listOf(
                ParticleConnectAdapter(),
                ParticleWalletConnectAdapter(),
                MetaMaskConnectAdapter(),
                RainbowConnectAdapter(),
                TrustConnectAdapter(),
                ImTokenConnectAdapter(),
                BitKeepConnectAdapter(),
                WalletConnectAdapter(),
                PhantomConnectAdapter(),
                EVMConnectAdapter(),
                SolanaConnectAdapter(),
                TokenPocketConnectAdapter(),
                Coin98ConnectAdapter(),
                BitpieConnectAdapter(),
                AlphaWalletConnectAdapter(),
            )
        }

    }
}