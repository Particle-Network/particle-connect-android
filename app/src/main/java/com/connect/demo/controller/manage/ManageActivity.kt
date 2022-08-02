package com.connect.demo.controller.manage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.connect.common.ConnectCallback
import com.connect.common.IConnectAdapter
import com.connect.common.model.Account
import com.connect.common.model.ConnectError
import com.connect.demo.R
import com.connect.demo.base.BaseActivity
import com.connect.demo.controller.secret.ImportWalletActivity
import com.connect.demo.databinding.ActivityManageBinding
import com.connect.demo.utils.toast
import com.evm.adapter.EVMConnectAdapter
import com.particle.connect.ParticleConnect
import com.solana.adapter.SolanaConnectAdapter

class ManageActivity : BaseActivity<ActivityManageBinding>(R.layout.activity_manage) {

    private val adapter = ConnectAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRv()
        setupToolbar()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRv() {
        binding.adapterRv.layoutManager = LinearLayoutManager(this)
        binding.adapterRv.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.adapterRv.setHasFixedSize(true)

        adapter.setOnItemClickListener { _, _, position ->
            val connectAdapter = adapter.data[position]
            connectWallet(connectAdapter)
        }
        binding.adapterRv.adapter = adapter
        adapter.setList(ParticleConnect.getAdapters())
    }

    private fun connectWallet(connectAdapter: IConnectAdapter) {
        when (connectAdapter) {
            is SolanaConnectAdapter -> {
                showImportMenu(connectAdapter, "solana")

            }
            is EVMConnectAdapter -> {
                showImportMenu(connectAdapter, "evm")
            }
            else -> {
                connectAdapter.connect(object : ConnectCallback {
                    override fun onConnected(account: Account) {
                        toast("connect success")
                    }

                    override fun onError(error: ConnectError) {
                        toast(error.message)
                    }
                })
            }
        }
    }

    private fun showImportMenu(connectAdapter: IConnectAdapter, chainType: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setIcon(R.drawable.ic_logo)
        alertDialog.setTitle("Particle Connect")

        alertDialog.setItems(
            arrayOf(
                "Import ${chainType.uppercase()} Wallet",
                "Create ${chainType.uppercase()} Wallet"
            )
        ) { _, which ->
            if (which == 0) {
                val intent = Intent(this, ImportWalletActivity::class.java)
                intent.putExtra("chainType", chainType)
                startActivity(intent)
            } else {
                createWallet(connectAdapter)
            }
        }

        alertDialog.setNegativeButton("Cancel", null)
        alertDialog.create().show()
    }

    private fun createWallet(connectAdapter: IConnectAdapter) {
        connectAdapter.connect(object : ConnectCallback {
            override fun onConnected(account: Account) {
                toast("Create wallet success")
            }

            override fun onError(error: ConnectError) {
                toast(error.message)
            }
        })
    }
}