package com.connect.demo.controller.reference

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.connect.common.*
import com.connect.common.eip4361.Eip4361Message
import com.connect.common.eip4361.Web3jSignatureVerifier
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
import com.particle.api.evm
import com.particle.api.service.data.ContractParams
import com.particle.base.ParticleNetwork
import com.particle.base.model.ITxData
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
        binding.signMessageVerify.setOnClickListener {
            val message = createMessage()
            if (walletAccount.connectAdapter.connected(walletAccount.account.publicAddress)) {
                login(message)
            } else {
                connectWallet()
            }

        }
        binding.writeContract.setOnClickListener {
            lifecycleScope.launch {
                val params = ContractParams.customAbiEncodeFunctionCall(
                    contractAddress = "0xd000f000aa1f8accbd5815056ea32a54777b2fc4",
                    methodName = "mint",
                    params = listOf("1")
                )
                val txData: ITxData? = ParticleNetwork.evm.writeContract(
                    walletAccount.account.publicAddress,
                    params
                )
                walletAccount.connectAdapter.signAndSendTransaction(
                    walletAccount.account.publicAddress,
                    txData!!.serialize(),
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
        }
    }


    private fun connectWallet() {
        walletAccount.connectAdapter.connect(null, object : ConnectCallback {
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

//    val testTransactionStr ="7b22616374696f6e223a226e6f726d616c222c22636861696e4964223a2230783261222c2264617461223a223078613037313264363830303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303031222c2266726f6d223a22307835303466383364363530323966623630376663616134336562643062373032326162313631623063222c226761734c6576656c223a22222c226761734c696d6974223a2230783230633835222c226d6178466565506572476173223a2230783737333539343063222c226d61785072696f72697479466565506572476173223a2230783737333539343030222c226e6f6e6365223a22307830222c2272223a6e756c6c2c2273223a6e756c6c2c22746f223a22307864303030663030306161316638616363626435383135303536656133326135343737376232666334222c2274797065223a22307832222c2276223a6e756c6c2c2276616c7565223a6e756c6c7d"

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
//        val transactions = listOf(
//            MockManger.mockCreateTransaction(walletAccount.account.publicAddress),
//            MockManger.mockCreateTransaction(walletAccount.account.publicAddress)
//        )
//        binding.request.text = GsonUtils.toJson(transactions)
        val transactions = Array(1){"3igAAYeQWiGxrTXP47oHcCqgbLeYt9uHEmzhR7ZXNfffL9kGxgoCpw4qsBwby6y7kCJC2JDRopPvFtqs4C377ygMb6z4rcodDfB5Qt1Wz6iTXBVGht462fGWcumarJx6J5tnMpzRN8DffZfwuaJFxuE9WSora2cXWWyRW2Ps7n2A5NGv2ULPMGumkzPaw3mUyLfDQDndrgxHPYaWzogMxWdwTQ76t44ZBBqhswoTx1KkqYku1tL1p2qnhtPZcS73czrBuhprqwLNSo5tP5ijRvUM8x9qeMhtUvsSZ9Le5y27GaGFHznB4ET85KfyujVtWbSWeHTRyDkzoR2EEHpfm6gw6DHj9o7f72c5rWrtjaDe7CFdUuKYcJcQDWwHnKgn3A2s1iHnv4ZN4sm5HXuGNTYMdxuLZdbvZzmHN3Ai4dn2vVKFks7uYcv15AKnfj9mv2RLXSLkubrP5GCymPyhZ1HUxk7qjVHJFQ89w2q71fzvPmhnMU6vzTz2LTBoXttnTRWxBk818Pg1J2sUHaqKgvp77N9LGhf6aEE2d5XQ68dzRpm6NQRkRX4jF5oyDDWZ9x95q6WQTFeENdUZxPJFYy4WUqGqqRPMkphFbpDCPQAAKkmoNCbkehwPULMBQw3p4BRW7pQLs3cvDcPgxjtBUMQtkCqPNRAk7tvxPLM6EhcFYB9d9TV3BuQ9yxJvyorD9rCzyk8qCkCuNJc7S2NPsCDcMYDRSD8GhcMbjnuWdYwdcMj2xw6Gz5zB1rK9wGcAMJmefDFv89VoBDnktKVa4sWp9Y519YmpPRuN2CMaaiuwAqQyZJaeE5V7WZXZAMppNYntyevu4P4Ws2HPqG8wM49fuDf1au6ehhDRDrihTh3wdwouyVACEuAomjqMEYtYaSJMA6YChm6Y8vXposU4Svog7cCenpoHLY3rYPGaR5orqJS7rfNDmRLp8UKqQ4XLwdKkjoFXBPB9DeptAFstpRPGGsSpk4hqCAEy6Z59AJWaKfD51PcVaN88z53uwfhfoWTKAecqAx9u2YctpueWrD9KMscXxjLbNgz7USSN3CFCtBXERiawEEepgRPKHiUhejFfpECoXhMDR36TmBusQ5TZ4fyoffwQDpf1L3iNcxcVv23YXnHqgC3dJunJQmkajUYS9qMokjJUBTtwbi9ZUcTGDtAGwKmsvsDetwn3WYVhLVtmSPF33URKQ3zH7CKAVEEdmUfVEboDB3mv5CuV8aJ8f23qzuqA8qUDbF3MYNTYDgPyN2Z8vQStxKQSbtu3uvzuW6rPKHjSwvm6RmHziVPsrNqVabyean9Nx1Gdf1fN6BYSN9DcW9EoFaTb2ucFFwz3yFgVePzEjezLi346xYZDwuYH5unkFA8eczYwSVpy7rNy6uKMkWR4REVubeLuHnkMTYucGkjFoyP6JCow6P2xb7cv2YA5ngXeQ5xQ1enAnqgKMTjxDfRSX4Wj5sBPJhyZ47gTkuT5GKZYpo5DuJ8ksvB91hPq3U4YbvKmVrvdEp"}
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

    private fun signMessage(message: String = "Hello Particle Connect!") {
        binding.request.text = message
        binding.result.text = ""
        walletAccount.connectAdapter.signMessage(
            walletAccount.account.publicAddress,
            MockManger.encode(message),
            object : SignCallback {
                override fun onSigned(signature: String) {
                    binding.result.text = signature
                    toast("signMessage success")
                    LogUtils.d(
                        "signMessage",
                        signature
                    )
                    if (ParticleConnect.chainType == ChainType.EVM) {
                        val address = Web3jSignatureVerifier.recoverAddressFromSignature(
                            signature,
                            message
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

    private fun login(eip4361Message: Eip4361Message) {
        val message = eip4361Message.toString()
        binding.request.text = eip4361Message.toString()
        binding.result.text = ""
        walletAccount.connectAdapter.login(
            walletAccount.account.publicAddress,
            eip4361Message,
            object : SignCallback {
                override fun onSigned(signature: String) {
                    val result = walletAccount.connectAdapter.verify(
                        walletAccount.account.publicAddress,
                        signature,
                        message
                    )
                    val displayTxt = "signature:$signature\n\n verify:$result"
                    LogUtils.d("login verify", result)
                    binding.result.text = displayTxt
                }

                override fun onError(error: ConnectError) {
                    toast(error.message)
                }
            })
    }

    private fun createMessage(): Eip4361Message {
        // Message example from
        // evm-> https://eips.ethereum.org/EIPS/eip-4361
        // sol-> https://github.com/ChainAgnostic/CAIPs/blob/master/CAIPs/caip-74.md
        /*  val msg = Eip4361Message.fromString(
              """
              particle.network wants you to sign in with your Ethereum account:
              0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc21

              I accept the ServiceOrg Terms of Service: https://service.org/tos

              URI: https://service.org/login
              Version: 1
              Chain ID: 1
              Nonce: 32891756
              Issued At: 2021-09-30T16:25:24Z
              Resources:
              - ipfs://bafybeiemxf5abjwjbikoz4mc3a3dla6ual3jsgpdr4cjr3oz3evfyavhwq/
              - https://example.com/my-web2-claim.json
              """.trimIndent()
          )*/
        //you can use val msg = Eip4361Message("xx"...)
        val msg = Eip4361Message.createWithRequiredParameter(
            "particle.network",
            "https://service.org/login",
            "0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc21"
        )
        return msg
    }

}