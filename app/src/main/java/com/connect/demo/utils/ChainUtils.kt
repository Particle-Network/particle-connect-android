package com.connect.demo.utils

import com.particle.base.*

object ChainUtils {
    private val chainInfos = mutableListOf<ChainInfo>()

    fun getAllChains(): List<ChainInfo> {
        if (chainInfos.isNotEmpty()) return chainInfos

        chainInfos.apply {
            add(EthereumChain(EthereumChainId.Mainnet))
            add(EthereumChain(EthereumChainId.Kovan))

            add(BscChain(BscChainId.Mainnet))
            add(BscChain(BscChainId.Testnet))

            add(PolygonChain(PolygonChainId.Mainnet))
            add(PolygonChain(PolygonChainId.Mumbai))

            add(AvalancheChain(AvalancheChainId.Mainnet))
            add(AvalancheChain(AvalancheChainId.Testnet))

            add(MoonbeamChain(MoonbeamChainId.Mainnet))
            add(MoonbeamChain(MoonbeamChainId.Testnet))

            add(MoonriverChain(MoonriverChainId.Mainnet))
            add(MoonriverChain(MoonriverChainId.Testnet))

            add(HecoChain(HecoChainId.Mainnet))
            add(HecoChain(HecoChainId.Testnet))

            add(FantomChain(FantomChainId.Mainnet))
            add(FantomChain(FantomChainId.Testnet))

            add(ArbitrumChain(ArbitrumChainId.Mainnet))
            add(ArbitrumChain(ArbitrumChainId.Testnet))

            add(HarmonyChain(HarmonyChainId.Mainnet))
            add(HarmonyChain(HarmonyChainId.Testnet))

            add(AuroraChain(AuroraChainId.Mainnet))
            add(AuroraChain(AuroraChainId.Testnet))

            add(SolanaChain(SolanaChainId.Mainnet))
            add(SolanaChain(SolanaChainId.Testnet))
            add(SolanaChain(SolanaChainId.Devnet))
        }
        return chainInfos
    }

}