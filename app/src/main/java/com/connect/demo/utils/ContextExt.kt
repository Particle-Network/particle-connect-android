package com.connect.demo.utils

import android.content.Context
import android.widget.Toast

/**
 * Created by chaichuanfa on 2022/7/25
 */
fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.toast(resId: Int) {
    Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show()
}