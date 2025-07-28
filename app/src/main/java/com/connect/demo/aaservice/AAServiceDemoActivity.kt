package com.connect.demo.aaservice

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import auth.core.adapter.AuthCoreAdapter
import com.connect.demo.R
import com.connect.demo.databinding.ActivityAaServiceBinding
import com.particle.base.ParticleNetwork
import com.particle.connect.ParticleConnect
import kotlinx.coroutines.launch
import network.blankj.utilcode.util.LogUtils

class AAServiceDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAaServiceBinding
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_aa_service)
        setupUI()
        initializeDefaultData()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.request.movementMethod = ScrollingMovementMethod.getInstance()
        binding.result.movementMethod = ScrollingMovementMethod.getInstance()
        setupButtonClickListeners()
    }

    private fun setupButtonClickListeners() {
        binding.test.setOnClickListener { executeWithLoading { test() } }

    }

    var currentAddress = ""
    private fun initializeDefaultData() {
        val authCoreAdapter = ParticleConnect.getAdapters().first { it is AuthCoreAdapter }
        val accounts = authCoreAdapter.getAccounts().map {
            it.publicAddress
        }
        LogUtils.d(accounts[0].toString())
        if (authCoreAdapter.getAccounts().isEmpty()) {
            updateDisplay("No AuthCore Connected ")
        } else {
            updateDisplay(accounts[0].toString())
            currentAddress = accounts[0].toString()
            updateDisplay("currentAddress:$currentAddress")
        }

    }

    private fun executeWithLoading(action: suspend () -> Unit) {
        if (isLoading) return
        lifecycleScope.launch {
            try {
                isLoading = true
                action()
            } catch (e: Exception) {
                Log.e("AAServiceDemo", "Error executing action", e)
                showToast("Error: ${e.message}")
                updateDisplay("Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun test() {
        try {
            // Initialize with chain info (using Ethereum Sepolia like RN version)
            val smartAccount = ParticleNetwork.getAAService().getSmartAccount(currentAddress)
            LogUtils.d("result:", smartAccount?.smartAccountAddress)
            updateDisplay("smartAccountAddress:${smartAccount?.smartAccountAddress}")
        } catch (e: Exception) {
            updateDisplay("❌ Initialization failed: ${e.message}")
            throw e
        }
    }


    private fun updateDisplay(text: String) {
        binding.request.text = text
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}