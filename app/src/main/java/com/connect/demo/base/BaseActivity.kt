package com.connect.demo.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding


open class BaseActivity<BD : ViewDataBinding>(private val layoutId: Int) : AppCompatActivity() {

    internal lateinit var binding: BD
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
    }
}