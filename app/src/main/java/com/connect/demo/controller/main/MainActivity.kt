package com.connect.demo.controller.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.connect.common.DisconnectCallback
import com.connect.common.ILocalAdapter
import com.connect.common.model.ConnectError
import com.connect.common.utils.PrefUtils
import com.connect.demo.R
import com.connect.demo.base.BaseActivity
import com.connect.demo.controller.manage.ManageActivity
import com.connect.demo.controller.reference.ReferenceActivity
import com.connect.demo.databinding.ActivityMainBinding
import com.connect.demo.model.WalletAccount
import com.connect.demo.utils.ChainUtils
import com.connect.demo.utils.MockManger
import com.connect.demo.utils.toast
import com.particle.connect.ParticleConnect
import kotlinx.coroutines.launch
import network.particle.chains.ChainInfo


class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private var selectChain = 1

    private val adapter: AccountAdapter = AccountAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupData()
        setupToolbar()
        setupChangeChain()
        setupRv()
    }

    override fun onResume() {
        super.onResume()
        refreshAccount()
    }

    private fun setupData() {
        selectChain = PrefUtils.getSettingInt("current_selected_chain", 1)
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.toolbar_action)
        binding.toolbar.setOnMenuItemClickListener {
            startActivity(Intent(this, ManageActivity::class.java))
            true
        }

        updateCurrentChain(ChainUtils.getAllChains()[selectChain])
    }

    private fun setupRv() {
        binding.accountRv.layoutManager = LinearLayoutManager(this)
        binding.accountRv.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.accountRv.adapter = adapter
        adapter.setOnItemClickListener { _, _, position ->
            val walletAccount = adapter.data[position]
            if (walletAccount.connectAdapter.supportChains.contains(ParticleConnect.chainType)) {
                MockManger.walletAccount = walletAccount
                startActivity(Intent(this, ReferenceActivity::class.java))
            } else {
                toast("The wallet not support current chain")
            }
        }
        adapter.setOnItemChildClickListener { _, _, position ->
            val walletAccount = adapter.data[position]
            showAccountMenu(walletAccount)
        }
    }

    private fun showAccountMenu(walletAccount: WalletAccount) {
        AlertDialog.Builder(this).apply {
            setIcon(R.drawable.ic_logo)
            setTitle(walletAccount.name)
            val items = mutableListOf("Copy address", "Rename wallet", "Disconnect")
            if (!walletAccount.account.mnemonic.isNullOrEmpty() || walletAccount.connectAdapter is ILocalAdapter) {
                items.add("Export Wallet")
            }

            setItems(items.toTypedArray()) { _, which ->
                when (which) {
                    0 -> copyWalletAddress(walletAccount)
                    1 -> renameWallet(walletAccount)
                    2 -> disconnectWallet(walletAccount)
                    3 -> exportWallet(walletAccount)
                }
            }
            setNegativeButton("Cancel", null)
            create().show()
        }
    }

    private fun copyWalletAddress(walletAccount: WalletAccount) {
        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText(packageName, walletAccount.account.publicAddress))
        toast("Copied to clipboard")
    }

    private fun renameWallet(walletAccount: WalletAccount) {
        AlertDialog.Builder(this).apply {
            setIcon(R.drawable.ic_logo)
            setTitle("Wallet Name")
            val view = layoutInflater.inflate(R.layout.edit_wallet_name_layout, null)
            val editView = view.findViewById<EditText>(R.id.edit)
            editView.setText(walletAccount.name)
            setView(view)
            setNegativeButton("Cancel", null)
            setPositiveButton("OK") { _, _ ->
                val name = editView.text.trim()
                if (name.isNotEmpty()) {
                    PrefUtils.setSettingString(
                        "${walletAccount.account.publicAddress}_wallet_name",
                        name.toString()
                    )
                    refreshAccount()
                }
            }
            create().show()
        }
    }

    private fun disconnectWallet(walletAccount: WalletAccount) {
        val address = walletAccount.account.publicAddress
        walletAccount.connectAdapter.disconnect(
            address,
            object : DisconnectCallback {
                override fun onDisconnected() {
                    toast("Wallet Disconnected")
                    PrefUtils.remove("${address}_wallet_name")
                    refreshAccount()
                }

                override fun onError(error: ConnectError) {
                    toast(error.message)
                }
            })
    }

    private fun exportWallet(walletAccount: WalletAccount) {
        lifecycleScope.launch {
            var message = ""
            val mnemonic = walletAccount.account.mnemonic
            if (!mnemonic.isNullOrEmpty()) {
                message += "Mnemonic:\n$mnemonic\n\n"
            }
            if (walletAccount.connectAdapter is ILocalAdapter) {
                val privateKey =
                    walletAccount.connectAdapter.exportWalletPrivateKey(walletAccount.account.publicAddress)
                if (privateKey.isNullOrEmpty()) {
                    toast("Export Private Key Error")
                } else {
                    message += "Private Key:\n$privateKey\n"
                    Log.d("privateKey", privateKey)
                }
            }
            AlertDialog.Builder(this@MainActivity).apply {
                setIcon(R.drawable.ic_logo)
                setTitle(walletAccount.name)
                setMessage(message)
                setPositiveButton("OK", null)
                create().show()
            }
        }
    }

    private fun refreshAccount() {
        val adapterAccounts = ParticleConnect.getAccounts()
        val walletAccounts = mutableListOf<WalletAccount>()
        adapterAccounts.forEach { adapterAccount ->
            adapterAccount.accounts.forEach {
                val name =
                    PrefUtils.getSettingString(
                        "${it.publicAddress}_wallet_name",
                        "${it.name} Wallet"
                    )!!
                walletAccounts.add(WalletAccount(name, it, adapterAccount.connectAdapter))
            }
        }
        adapter.setList(walletAccounts)
    }

    private fun updateCurrentChain(chain: ChainInfo) {
        val name = chain.name
        binding.chainName.text = name
        binding.chainId.text = chain.id.toString()
        binding.chainName.setCompoundDrawablesRelativeWithIntrinsicBounds(
            resources.getIdentifier(
                name.lowercase(),
                "drawable",
                packageName
            ), 0, 0, 0
        )

        ParticleConnect.setChain(chain)
        refreshAccount()
    }

    private fun setupChangeChain() {
        binding.changeChain.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setIcon(R.drawable.ic_logo)
            alertDialog.setTitle("Choose Chain")
            val listItems =
                ChainUtils.getAllChains().map {
                    it.fullname + "-" + it.id.toString()
                }.toTypedArray()

            alertDialog.setSingleChoiceItems(listItems, selectChain) { dialog, which ->
                selectChain = which
                updateCurrentChain(ChainUtils.getAllChains()[which])
                PrefUtils.setSettingInt("current_selected_chain", selectChain)
                dialog.dismiss()
            }
            alertDialog.setNegativeButton("Cancel", null)
            alertDialog.create().show()
        }
    }

}