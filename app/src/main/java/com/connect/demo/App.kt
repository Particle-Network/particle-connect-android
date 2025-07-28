package com.connect.demo

import android.app.Application
import auth.core.adapter.AuthCoreAdapter
import com.connect.demo.aaservice.AAServiceDemoActivity
import com.connect.demo.utils.CoilLoader
import com.evm.adapter.EVMConnectAdapter
import com.particle.base.Env
import com.particle.base.ParticleNetwork
import com.particle.base.model.DAppMetadata
import com.particle.connect.ParticleConnect
import com.particle.erc4337.ParticleNetworkAA
import com.particle.erc4337.ParticleNetworkAA.initAAMode
import com.particle.erc4337.aa.SimpleV2AAService
import com.phantom.adapter.PhantomConnectAdapter
import com.solana.adapter.SolanaConnectAdapter
import com.wallet.connect.adapter.BitKeepConnectAdapter
import com.wallet.connect.adapter.ImTokenConnectAdapter
import com.wallet.connect.adapter.MetaMaskConnectAdapter
import com.wallet.connect.adapter.OKXConnectAdapter
import com.wallet.connect.adapter.ParticleWalletConnectAdapter
import com.wallet.connect.adapter.RainbowConnectAdapter
import com.wallet.connect.adapter.TrustConnectAdapter
import com.wallet.connect.adapter.WalletConnectAdapter
import network.particle.chains.ChainInfo
import network.particle.chains.ChainInfo.Companion.Ethereum
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
        val chainInfo = ChainInfo.SeiDevnet
        ParticleNetwork.init(this, Env.DEV, chainInfo)
        ParticleNetwork.initAAMode(SimpleV2AAService)
        ParticleNetwork.getAAService().enableAAMode()
        ParticleConnect.init(
            this, Env.DEV, chainInfo, DAppMetadata(
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
                ParticleConnectAdapter(),
                MetaMaskConnectAdapter(),
                RainbowConnectAdapter(),
                TrustConnectAdapter(),
                PhantomConnectAdapter(),
                WalletConnectAdapter(),
                ImTokenConnectAdapter(),
                BitKeepConnectAdapter(),
                OKXConnectAdapter(),
                EVMConnectAdapter(),
                SolanaConnectAdapter(),
            )
        }

    }
}