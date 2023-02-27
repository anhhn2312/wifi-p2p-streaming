package com.andyha.coreui.base.ui.widget.recyclerview

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager

abstract class EndlessScrollDownListener(private val layoutManager: LayoutManager) :
    RecyclerView.OnScrollListener() {
    private var isLoading = false
    private var previousTotalItemCount = 0
    private val visibleThreshold = 1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy <= 0) return

        val totalItemCount = layoutManager.itemCount
        var lastVisibleItemCount = 0
        if (layoutManager is LinearLayoutManager) {
            lastVisibleItemCount = layoutManager.findLastVisibleItemPosition()
        } else if (layoutManager is GridLayoutManager) {
            lastVisibleItemCount = layoutManager.findLastVisibleItemPosition()
        }

        //if it's still loading and current total item > previous total item -> disable loading
        if (isLoading && totalItemCount > previousTotalItemCount) {
            isLoading = false
            previousTotalItemCount = totalItemCount
        }

        //if it is not loading and users reach the threshold of loading more -> load more data
        val isReachThreshold = lastVisibleItemCount + visibleThreshold >= totalItemCount
        if (!isLoading && isReachThreshold) {
            onLoadMore()
            isLoading = true
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        onStateChanged(newState)
    }

    fun onStateChanged(newState: Int) {

    }

    fun setLoaded() {
        isLoading = false
    }

    abstract fun onLoadMore()
}