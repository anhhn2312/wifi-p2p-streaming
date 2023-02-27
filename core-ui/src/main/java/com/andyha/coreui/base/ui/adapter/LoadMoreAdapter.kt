package com.andyha.coreui.base.ui.adapter

import androidx.recyclerview.widget.RecyclerView


abstract class LoadMoreAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    companion object {
        const val TYPE_ITEM = 5555
        const val TYPE_LOADING = 6100
    }

    protected var mItems: ArrayList<T> = ArrayList()

    protected var mShowLoadingItem = false

    override fun getItemCount(): Int {
        return mItems.size + if (mShowLoadingItem) getLoadMoreItemCount() else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= mItems.size) {
            TYPE_LOADING
        } else {
            TYPE_ITEM
        }
    }

    open fun getLoadMoreItemCount(): Int {
        return 1
    }

    fun showLoadMoreItems(loading: Boolean) {
        if (loading) {
            mShowLoadingItem = true
            notifyItemRangeInserted(mItems.size, getLoadMoreItemCount())
        } else {
            mShowLoadingItem = false
            notifyItemRangeRemoved(mItems.size, getLoadMoreItemCount())
        }
    }

    fun isLoadMore(): Boolean {
        return mShowLoadingItem
    }

    fun setData(items: List<T>) {
        val oldSize = mItems.size
        mItems.clear()
        notifyItemRangeRemoved(0, oldSize)

        mItems.addAll(items)
        notifyItemRangeInserted(0, items.size)
    }
}