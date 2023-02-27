package com.andyha.coreui.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

abstract class BaseListAdapter<T>(diff: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, BaseViewHolder<T>>(diff) {

    abstract fun getViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup): ViewBinding

    abstract fun createViewHolder(viewBinding: ViewBinding): BaseViewHolder<T>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewBinding = getViewBinding(layoutInflater, parent)
        return createViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) =
        holder.bind(getItem(position))

}