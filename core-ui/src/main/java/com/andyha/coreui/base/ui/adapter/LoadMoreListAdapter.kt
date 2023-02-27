package com.andyha.coreui.base.ui.adapter

import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


abstract class LoadMoreListAdapter<T, VH : RecyclerView.ViewHolder> : ListAdapter<T, VH> {

    companion object {
        const val TYPE_ITEM = 5555
        const val TYPE_LOADING = 6100
    }

    protected var mShowLoadingItem = false

    protected constructor(diffCallback: DiffUtil.ItemCallback<T>) : super(diffCallback) {

    }

    protected constructor(config: AsyncDifferConfig<T>) : super(config) {

    }

    override fun getItemCount(): Int {
        return currentList.size + if (mShowLoadingItem) getLoadMoreItemCount() else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= currentList.size) {
            TYPE_LOADING
        } else {
            TYPE_ITEM
        }
    }

    open fun getLoadMoreItemCount(): Int {
        return 1
    }

    fun showLoadMoreItems(loading: Boolean) {
        if (mShowLoadingItem == loading) {
            return
        }

        if (loading) {
            mShowLoadingItem = true
            notifyItemRangeInserted(currentList.size, getLoadMoreItemCount())
        } else {
            mShowLoadingItem = false
            notifyItemRangeRemoved(currentList.size, getLoadMoreItemCount())
        }
    }

    fun isLoadMore(): Boolean {
        return mShowLoadingItem
    }
}