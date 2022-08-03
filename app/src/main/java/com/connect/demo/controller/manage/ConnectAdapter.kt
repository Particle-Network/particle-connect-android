package com.connect.demo.controller.manage

import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.connect.common.IConnectAdapter
import com.connect.demo.R
import com.connect.demo.databinding.ItemAdapterBinding

/**
 * Created by chaichuanfa on 2022/7/25
 */
class ConnectAdapter :
    BaseQuickAdapter<IConnectAdapter, BaseDataBindingHolder<ItemAdapterBinding>>(R.layout.item_adapter) {

    override fun convert(holder: BaseDataBindingHolder<ItemAdapterBinding>, item: IConnectAdapter) {
        holder.dataBinding?.apply {
            icon.load(item.icon)
            name.text = item.name
        }
    }
}