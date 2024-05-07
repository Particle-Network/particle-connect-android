package com.connect.demo.utils

import network.particle.chains.ChainInfo

/**
 * Created by chaichuanfa on 2022/7/25
 */
object ChainUtils {

    fun getAllChains(): List<ChainInfo> {
        return ChainInfo.ParticleChains.values.toList()
    }

}