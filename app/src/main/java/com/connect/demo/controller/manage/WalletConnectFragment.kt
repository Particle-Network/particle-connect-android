package com.connect.demo.controller.manage

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope

import com.connect.demo.R
import com.connect.demo.databinding.FragmentWalletConnectBinding
import com.connect.demo.utils.BarcodeEncoder
import com.connect.demo.utils.QrParams
import com.google.zxing.BarcodeFormat
import com.particle.base.ParticleNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ReceiveData(
    val tokenAddress: String?,
) : Parcelable

internal class WalletConnectFragment : DialogFragment() {

    companion object {
        private const val DATA_KEY: String = "DATA_KEY"

        fun show(fm: FragmentManager, data: String): WalletConnectFragment {
            val frag = WalletConnectFragment()
            val bundle = Bundle()
            bundle.putString(DATA_KEY, data)
            frag.arguments = bundle
            frag.show(fm, WalletConnectFragment::javaClass.name)
            return frag
        }


    }

    lateinit var binding: FragmentWalletConnectBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWalletConnectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireArguments().getString(DATA_KEY)?.let {
            lifecycleScope.launch {
                try {
                    val qr = generateQrCode(it)
                    binding.ivQrCode.setImageBitmap(qr)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun generateQrCode(address: String): Bitmap = withContext(Dispatchers.Default) {
        BarcodeEncoder.encodeBitmap(
            address,
            BarcodeFormat.QR_CODE,
            900,
            900,
            QrParams(
                ContextCompat.getColor(ParticleNetwork.context, R.color.black),
                ContextCompat.getColor(ParticleNetwork.context, R.color.white)
            )
        )
    }

}