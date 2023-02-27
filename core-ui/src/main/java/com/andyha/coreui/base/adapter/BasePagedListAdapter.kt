package com.andyha.coreui.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BasePagedListAdapter<T : Any, VB : ViewBinding, VH : BasePagingViewHolder<T, VB>>(
    diffCallback: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, VH>(diffCallback) {

    abstract fun setupViewContentBinding(inflater: LayoutInflater, viewGroup: ViewGroup): VB
    abstract fun createItemViewHolder(itemViewBinding: ViewBinding): VH

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding: ViewBinding = setupViewContentBinding(layoutInflater, parent)
        return createItemViewHolder(itemBinding)
    }
}

abstract class BasePagingViewHolder<T, VB : ViewBinding>(itemViewBinding: VB) :
    RecyclerView.ViewHolder(itemViewBinding.root) {
    open fun bindData(t: T, position: Int) {
        //for implementation
    }
}