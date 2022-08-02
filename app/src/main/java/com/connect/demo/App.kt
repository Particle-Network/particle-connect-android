package com.connect.demo

import android.app.Application
import com.connect.common.model.DAppMetadata
import com.evm.adapter.EVMConnectAdapter
import com.particle.base.Env
import com.particle.base.EthereumChain
import com.particle.base.EthereumChainId
import com.particle.connect.ParticleConnect
import com.particle.connect.ParticleConnectAdapter
import com.phantom.adapter.PhantomConnectAdapter
import com.solana.adapter.SolanaConnectAdapter
import com.wallet.connect.adapter.*


class App : Application() {

    private lateinit var instance: App

    override fun onCreate() {
        super.onCreate()
        instance = this
        ParticleConnect.init(
            this,
            Env.DEV,
            EthereumChain(EthereumChainId.Kovan),
            DAppMetadata(
                "Particle Connect",
                "https://static.particle.network/wallet-icons/Particle.png",
                "https://particle.network"
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
            )
        }
    }
}