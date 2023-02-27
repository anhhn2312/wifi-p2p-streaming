package com.andyha.coreui.base.adapter.multiSectionsSelection

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


abstract class BaseSectionHeaderViewHolder<H>(viewBinding: ViewBinding) :
    RecyclerView.ViewHolder(viewBinding.root) {
    abstract fun bind(item: H)
}