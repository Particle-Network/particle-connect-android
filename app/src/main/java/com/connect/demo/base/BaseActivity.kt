package com.connect.demo.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Created by chaichuanfa on 2022/7/25
 */
open class BaseActivity<BD : ViewDataBinding>(private val layoutId: Int) : AppCompatActivity() {

    internal lateinit var binding: BD
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
    }
}