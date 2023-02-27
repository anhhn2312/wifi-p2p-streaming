package com.andyha.coreui.base.adapter.multiSectionsSelection

import android.view.View
import android.widget.CheckBox
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


abstract class BaseSectionItemViewHolder<T>(
    private val viewBinding: ViewBinding,
    private val onItemClicked: ((Int) -> Unit)? = null
) : RecyclerView.ViewHolder(viewBinding.root) {

    private var divider: View? = null
    private var checkbox: CheckBox? = null

    init {
        checkbox = getCheckboxId()?.let { viewBinding.root.findViewById(it) }
        divider = getDividerId()?.let { viewBinding.root.findViewById(it) }
    }

    fun bind(
        item: T,
        selected: Boolean,
        enabled: Boolean,
        itemPosition: ItemPosition
    ) {
        viewBinding.apply {
            divider?.isVisible = itemPosition.dividerEnabled
            root.setBackgroundResource(itemPosition.background)
            root.setOnClickListener { onItemClicked?.invoke(adapterPosition) }
        }
        bindItemInternal(item)
        bindSelection(selected)
        bindEnable(enabled)
    }

    fun bindSelection(selected: Boolean) {
        checkbox?.isChecked = selected
    }

    fun bindEnable(enabled: Boolean) {
        viewBinding.apply {
            root.isEnabled = enabled
        }
    }

    abstract fun getCheckboxId(): Int?

    abstract fun getDividerId(): Int?

    abstract fun bindItemInternal(item: T)
}