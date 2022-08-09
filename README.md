[![Maven Central](https://maven-badges.herokuapp.com/maven-central/network.particle/connect/badge.svg?style=flat)](https://search.maven.org/artifact/network.particle/connect)

# Particle Connect Android
The best way to connect a wallet. Support multi chains and multi wallet. Learn more visit [Particle Network](https://docs.particle.network/).

<img width="420" src="https://static.particle.network/docs-images/add-wallet.png"></img>
<img width="420" src="https://static.particle.network/docs-images/import-private-key.png"></img>    

## Summary

Modular Kotlin wallet adapters and components for EVM & Solana chains. Manage wallet and custom RPC request.

![Particle Connect](https://static.particle.network/docs-images/particle-connect.jpeg)

## Quick Start

```
dependencies {
     //required dependencies
    implementation("network.particle:auth-service:{latest-version}")
    implementation 'network.particle:connect:{latest-version}'
    
    //Optional: support evm chain Generate & Import wallet
    implementation 'network.particle:connect-evm-adapter:{latest-version}'
    
    //Optional: support solana chain Generate & Import wallet
    implementation 'network.particle:connect-solana-adapter:{latest-version}'
    
    //Optional: support connect Phantom wallet
    implementation 'network.particle:connect-phantom-adapter:{latest-version}'
    
    //Optional: support WalletConnect Protocol, include MetaMask, Rainbow, Trust, imToken etc.
    implementation 'network.particle:connect-wallet-connect-adapter:{latest-version}'
}
```

Add below config to AndroidManifest.xml

```
<application>
        <!--   Particle Network config start     -->
        <activity
                android:name="com.particle.network.controller.WebActivity"
                android:exported="true"
                android:launchMode="singleTask"
                android:theme="@android:style/Theme.Translucent.NoTitleBar.Fullscreen"
                >
            <intent-filter>
                <data android:scheme="pn${pn_app_id}" />
        
                <action android:name="android.intent.action.VIEW" />
        
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
            </intent-filter>
        </activity>
        
        <activity
            android:name="com.connect.common.controller.RedirectActivity"
            android:exported="true"
            android:launchMode="singleTask"
            android:theme="@android:style/Theme.Translucent.NoTitleBar.Fullscreen">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />

                <data android:scheme="connect${PN_APP_ID}" />
            </intent-filter>
        </activity>
        
        <meta-data
                android:name="particle.network.project_id"
                android:value="${pn_project_id}"
                />
        <meta-data
                android:name="particle.network.project_client_key"
                android:value="${pn_project_client_key}"
                />
        <meta-data
                android:name="particle.network.app_id"
                android:value="${pn_app_id}"
                />
        <!--   Particle Network config end     -->
</application>
```

Init Particle Connect in `Application#Create()`

```
ParticleConnect.init(
        this,
        Env.DEV,    //debug mode
        EthereumChain(EthereumChainId.Kovan),    //chain info
        DAppMetadata(
            "Particle Connect",
            "https://static.particle.network/wallet-icons/Particle.png",
            "https://particle.network"
        )    //DApp or Wallet info
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
        )  //list all support adapters, lazy create.
    }
```

Switch chain.

```
ParticleConnect.setChain(chain)
```

Get all wallet adapters.

```
var adapters = ParticleConnect.getAdapters(chainTypes)
//or
var adapters = ParticleConnect.getAdapterByAddress(address)
```

Get all connected accounts.

```
val accounts = ParticleConnect.getAccounts(chainTypes)
```

Connect wallet. (For `EVMConnectAdapter` or `SolanaConnectAdapter` will generate new wallet)

```
connectAdapter.connect(callback)
```

Disconnect wallet.

```
connectAdapter.disconnect(address, callback)
```

Check whether the account is connected.

```
val result = connectAdapter.connected(address)
```

Import wallet. (Only `EVMConnectAdapter` and `SolanaConnectAdapter` support this method)

```
// import wallet with private key
val account = connectAdapter.importWalletFromPrivateKey(privateKey)

// import wallet with mnemonic(Split with space).
val account = connectAdapter.importWalletFromMnemonic(mnemonic)
```

Export wallet. (Only `EVMConnectAdapter` and `SolanaConnectAdapter` support this method)

```
val privateKey = connectAdapter.exportWalletPrivateKey(address)
```

Sign and send transaction.

```
// todo: check connected before sign
connectAdapter.signAndSendTransaction(address, transaction, callback)
```

Sign transaction. (Only Solana chain support this method)

```
connectAdapter.signTransaction(address, transaction, callback)
```

Sign all transactions. (Only Solana chain support this method)

```
connectAdapter.signAllTransactions(address, transactions, callback)
```

Sign message. (EVM call `personal_sign`)

```
connectAdapter.signMessage(address, message, callback)
```

Sign typed data. (Only EVM chains support this method)

```
connectAdapter.signTypedData(address, data, callback)
```

## Give Feedback

You can join our [Discord](https://discord.gg/2y44qr6CR2).

