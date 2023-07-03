package com.connect.demo

import android.app.Application
import com.connect.common.model.DAppMetadata
import com.connect.demo.custom_connectadapter.Coin98ConnectAdapter
import com.connect.demo.utils.CoilLoader
import com.evm.adapter.EVMConnectAdapter
import com.particle.base.Env
import com.particle.base.EthereumChain
import com.particle.base.EthereumChainId
import com.particle.connect.ParticleConnect
import com.particle.connect.ParticleConnectAdapter
import com.phantom.adapter.PhantomConnectAdapter
import com.solana.adapter.SolanaConnectAdapter
import com.wallet.connect.adapter.*

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
            this,
            Env.DEV,
            EthereumChain(EthereumChainId.Goerli),
            DAppMetadata(
                "f431aaea6e4dea6a669c0496f9c009c1",
                "Particle Connect", "https://connect.particle.network/icons/512.png", "https://particle.network",
                description = "Particle Connect is a decentralized wallet connection solution that allows users to connect to DApps with their wallets.",
                redirect = "particlewc://"
            )
        ) {
            listOf(
                ParticleConnectAdapter(),
                MetaMaskConnectAdapter(),
                RainbowConnectAdapter(),
                TrustConnectAdapter(),
                ImTokenConnectAdapter(),
                BitKeepConnectAdapter(),
                WalletConnectAdapter(),
                PhantomConnectAdapter(),
                EVMConnectAdapter(),
                SolanaConnectAdapter(),
                Coin98ConnectAdapter(),
            )
        }
    }
}