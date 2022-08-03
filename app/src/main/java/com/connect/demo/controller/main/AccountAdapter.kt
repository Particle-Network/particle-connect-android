package com.connect.demo.controller.main

import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.connect.demo.R
import com.connect.demo.databinding.ItemAccountBinding
import com.connect.demo.model.WalletAccount

/**
 * Created by chaichuanfa on 2022/7/25
 */
class AccountAdapter :
    BaseQuickAdapter<WalletAccount, BaseDataBindingHolder<ItemAccountBinding>>(R.layout.item_account) {

    init {
        addChildClickViewIds(R.id.edit_account)
    }

    override fun convert(holder: BaseDataBindingHolder<ItemAccountBinding>, item: WalletAccount) {
        holder.dataBinding?.apply {
            icon.load(item.account.icons?.get(0))
            name.text = item.name
            address.text = item.account.publicAddress
        }
    }

}