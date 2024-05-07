package com.connect.demo.controller.secret

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.connect.common.ILocalAdapter
import com.connect.demo.R
import com.connect.demo.base.BaseActivity
import com.connect.demo.databinding.ActivityImportWalletBinding
import com.connect.demo.utils.toast
import com.evm.adapter.EVMConnectAdapter
import com.particle.connect.ParticleConnect
import com.solana.adapter.SolanaConnectAdapter
import kotlinx.coroutines.launch

class ImportWalletActivity :
    BaseActivity<ActivityImportWalletBinding>(R.layout.activity_import_wallet) {

    private var chainType: String = "evm"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chainType = intent.getStringExtra("chainType") ?: "evm"
        binding.toolbar.setNavigationOnClickListener {finish()  }

        binding.btImport.setOnClickListener {
            val secret = binding.secret.text.trim()
            if (secret.isNotEmpty()) {
                importWallet(secret.toString())
            } else {
                toast("Please input private key or mnemonic")
            }
        }
    }

    private fun importWallet(secret: String) {

        lifecycleScope.launch {
            try {
                val account = if (secret.contains(" ")) {
                    // import mnemonic
                    getAdapter().importWalletFromMnemonic(secret)
                } else {
                    // import private key
                    getAdapter().importWalletFromPrivateKey(secret)
                }
                if (account != null) {
                    toast("Import wallet success")
                } else {
                    toast("import wallet fail")
                }
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                toast("import wallet fail")
            }
        }

    }

    private fun getAdapter(): ILocalAdapter {
        if (chainType == "evm") {
            return ParticleConnect.getAdapters().first { it is EVMConnectAdapter } as ILocalAdapter
        }
        return ParticleConnect.getAdapters().first { it is SolanaConnectAdapter } as ILocalAdapter
    }
}