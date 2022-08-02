package com.connect.demo.controller.manage

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.connect.common.IConnectAdapter
import com.connect.demo.R
import com.connect.demo.databinding.ItemAdapterBinding

class ConnectAdapter :
    BaseQuickAdapter<IConnectAdapter, BaseDataBindingHolder<ItemAdapterBinding>>(R.layout.item_adapter) {

    override fun convert(holder: BaseDataBindingHolder<ItemAdapterBinding>, item: IConnectAdapter) {
        holder.dataBinding?.apply {
            Glide.with(icon)
                .load(item.icon)
                .centerCrop()
                .into(icon)
            name.text = item.name
        }
    }
}