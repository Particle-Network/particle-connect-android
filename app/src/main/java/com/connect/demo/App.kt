package com.connect.demo

import android.app.Application
import auth.core.adapter.AuthCoreAdapter
import com.connect.demo.utils.CoilLoader
import com.evm.adapter.EVMConnectAdapter
import com.particle.base.Env
import com.particle.base.model.DAppMetadata
import com.particle.connect.ParticleConnect
import com.phantom.adapter.PhantomConnectAdapter
import com.solana.adapter.SolanaConnectAdapter
import com.wallet.connect.adapter.BitGetConnectAdapter
import com.wallet.connect.adapter.ImTokenConnectAdapter
import com.wallet.connect.adapter.MetaMaskConnectAdapter
import com.wallet.connect.adapter.OKXConnectAdapter
import com.wallet.connect.adapter.RainbowConnectAdapter
import com.wallet.connect.adapter.TrustConnectAdapter
import com.wallet.connect.adapter.WalletConnectAdapter
import network.particle.chains.ChainInfo.Companion.EthereumSepolia
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
            this, Env.DEV, EthereumSepolia, DAppMetadata(
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
                AuthCoreAdapter(),
                MetaMaskConnectAdapter(),
                RainbowConnectAdapter(),
                TrustConnectAdapter(),
                ImTokenConnectAdapter(),
                BitGetConnectAdapter(),
                WalletConnectAdapter(),
                PhantomConnectAdapter(),
                EVMConnectAdapter(),
                SolanaConnectAdapter(),
                OKXConnectAdapter()
            )
        }
    }
}