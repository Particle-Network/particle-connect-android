package com.connect.demo.controller.reference

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.connect.common.*
import com.connect.common.model.Account
import com.connect.common.model.ChainType
import com.connect.common.model.ConnectError
import com.connect.common.utils.GsonUtils
import com.connect.common.utils.HexUtils
import com.connect.demo.R
import com.connect.demo.base.BaseActivity
import com.connect.demo.databinding.ActivityReferenceBinding
import com.connect.demo.model.WalletAccount
import com.connect.demo.utils.MockManger
import com.connect.demo.utils.StreamUtils
import com.connect.demo.utils.toast
import com.evm.adapter.EthersUtils
import com.particle.connect.ParticleConnect
import kotlinx.coroutines.launch

class ReferenceActivity : BaseActivity<ActivityReferenceBinding>(R.layout.activity_reference) {

    private lateinit var walletAccount: WalletAccount


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        walletAccount = MockManger.walletAccount!!
        setupUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        MockManger.walletAccount = null
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.toolbar.title = walletAccount.name
        binding.toolbar.inflateMenu(R.menu.delete_action)
        binding.toolbar.setOnMenuItemClickListener {
            walletAccount.connectAdapter.disconnect(walletAccount.account.publicAddress,
                object : DisconnectCallback {
                    override fun onDisconnected() {
                        finish()
                    }

                    override fun onError(error: ConnectError) {
                        toast(error.message)
                    }

                })
            true
        }

        binding.address.text = "Address: ${walletAccount.account.publicAddress}"
        binding.signAndSendTransaction.setOnClickListener {
            if (walletAccount.connectAdapter.connected(walletAccount.account.publicAddress)) {
                lifecycleScope.launch {
                    try {
                        signAndSendTransaction()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                connectWallet()
            }
        }
        binding.signTransaction.setOnClickListener {
            if (walletAccount.connectAdapter.connected(walletAccount.account.publicAddress)) {
                lifecycleScope.launch {
                    try {
                        signTransaction()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                connectWallet()
            }
        }

        binding.signAllTransactions.setOnClickListener {
            if (walletAccount.connectAdapter.connected(walletAccount.account.publicAddress)) {
                lifecycleScope.launch {
                    try {
                        signAllTransactions()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                connectWallet()
            }
        }
        binding.signMessage.setOnClickListener {
            if (walletAccount.connectAdapter.connected(walletAccount.account.publicAddress)) {
                signMessage()
            } else {
                connectWallet()
            }
        }
        binding.signTypedData.setOnClickListener {
            if (walletAccount.connectAdapter.connected(walletAccount.account.publicAddress)) {
                signTypedData()
            } else {
                connectWallet()
            }
        }
        binding.request.movementMethod = ScrollingMovementMethod.getInstance()
        binding.result.movementMethod = ScrollingMovementMethod.getInstance()
    }

    private fun connectWallet() {
        walletAccount.connectAdapter.connect(object : ConnectCallback {
            override fun onConnected(account: Account) {
                if (!account.publicAddress.equals(walletAccount.account.publicAddress, true)) {
                    toast("Please connect correct account")
                }
            }

            override fun onError(error: ConnectError) {
                toast(error.message)
            }
        })
    }

    private suspend fun signAndSendTransaction() {
        val transaction = MockManger.mockCreateTransaction(walletAccount.account.publicAddress)
        binding.request.text = GsonUtils.toJson(transaction)
        binding.result.text = ""
        walletAccount.connectAdapter.signAndSendTransaction(
            walletAccount.account.publicAddress,
            transaction,
            object : TransactionCallback {
                override fun onTransaction(transactionId: String?) {
                    binding.result.text = transactionId ?: ""
                    toast("signAndSendTransaction success")
                }

                override fun onError(error: ConnectError) {
                    Log.e("signAndSendTransaction", error.message)
                    toast(error.message)
                }
            })
    }

    private suspend fun signTransaction() {
        val transaction = MockManger.mockCreateTransaction(walletAccount.account.publicAddress)
        binding.request.text = GsonUtils.toJson(transaction)
        binding.result.text = ""
        walletAccount.connectAdapter.signTransaction(
            walletAccount.account.publicAddress,
            transaction,
            object : SignCallback {
                override fun onSigned(signature: String) {
                    binding.result.text = signature
                    toast("signTransaction success")
                }

                override fun onError(error: ConnectError) {
                    toast(error.message)
                }
            })
    }

    private suspend fun signAllTransactions() {
        val transactions = listOf(
            MockManger.mockCreateTransaction(walletAccount.account.publicAddress),
            MockManger.mockCreateTransaction(walletAccount.account.publicAddress)
        )
        binding.request.text = GsonUtils.toJson(transactions)
        binding.result.text = ""
        walletAccount.connectAdapter.signAllTransactions(
            walletAccount.account.publicAddress,
            transactions,
            object : SignAllCallback {
                override fun onSigned(signatures: List<String>) {
                    binding.result.text = GsonUtils.toJson(signatures)
                    toast("signAllTransactions success")
                }

                override fun onError(error: ConnectError) {
                    toast(error.message)
                }
            })
    }

    private fun signMessage() {
        val message = "Hello Particle Connect"
        binding.request.text = message
        binding.result.text = ""
        walletAccount.connectAdapter.signMessage(
            walletAccount.account.publicAddress,
            MockManger.encode(message),
            object : SignCallback {
                override fun onSigned(signature: String) {
                    binding.result.text = signature
                    toast("signMessage success")
                    if (ParticleConnect.chainType == ChainType.EVM) {
                        val address = EthersUtils.recoverAddressFromSignature(
                            signature,
                            HexUtils.encode(message.toByteArray())
                        )
                        LogUtils.d(
                            "recoverAddressFromSignature",
                            address,
                            walletAccount.account.publicAddress
                        )
                    }
                }

                override fun onError(error: ConnectError) {
                    toast(error.message)
                }
            })
    }

    private fun signTypedData() {
        val message = StreamUtils.getRawString(resources, R.raw.typed_data)
        binding.request.text = message
        binding.result.text = ""
        walletAccount.connectAdapter.signTypedData(
            walletAccount.account.publicAddress,
            MockManger.encode(message),
            object : SignCallback {
                override fun onSigned(signature: String) {
                    binding.result.text = signature
                    toast("signTypedData success")
                }

                override fun onError(error: ConnectError) {
                    toast(error.message)
                }
            })
    }

}