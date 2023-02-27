package com.andyha.coreui.base.adapter.multiSectionsSelection

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.andyha.coreutils.setOnDebounceClick


@Suppress("UNCHECKED_CAST")
abstract class BaseMultiSectionListAdapter<H : Any, T : Any>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val onItemSelected: ((T) -> Unit)? = null,
    private val onItemClicked: ((T) -> Unit)? = null
) : ListAdapter<Any, ViewHolder>(object : DiffUtil.ItemCallback<Any>() {

    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean = oldItem == newItem

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        if ((oldItem as? SectionItem<T>) != null && (newItem as? SectionItem<T>) != null) {
            return diffCallback.areContentsTheSame(oldItem.item, newItem.item)
        }
        return oldItem == newItem
    }
}) {

    // (HeaderId, T)
    var selectedItemsMap = mutableMapOf<Any, T>()

    // (HeaderId, List<ParamId>
    var disableItems = mapOf<Int, List<T>>()

    private lateinit var itemsList: MutableList<Any>

    private val onItemSelectedInternal: ((Int) -> Unit) = { position ->
        val selectedItem = getItem(position) as SectionItem<T>

        val prevSelectedPosition =
            itemsList.indexOfFirst { (it as? SectionItem<T>)?.item == selectedItemsMap[selectedItem.sectionId] }

        // Assign new selected item
        getInternalItem(position)?.let { selectedItemsMap[selectedItem.sectionId] = it }

        // Notify selection update for current and previous selected items
        notifyItemChanged(position, PAYLOAD_SELECTION)
        notifyItemChanged(prevSelectedPosition, PAYLOAD_SELECTION)

        onItemSelected?.invoke(selectedItem.item)
    }

    private val onItemClickedInternal: ((Int) -> Unit) = { position ->
        if (isItemSelectable()) {
            onItemSelectedInternal.invoke(position)
        } else {
            val selectedItem = getItem(position) as SectionItem<T>
            onItemClicked?.invoke(selectedItem.item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> createHeaderViewHolder(parent)
            else -> createItemViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is BaseSectionItemViewHolder<*> -> {
                val sectionItem = getItem(position) as SectionItem<T>
                (holder as? BaseSectionItemViewHolder<T>)?.bind(
                    sectionItem.item,
                    selectedItemsMap[sectionItem.sectionId] == getInternalItem(position),
                    disableItems[sectionItem.sectionId]?.contains(getInternalItem(position)) != true,
                    sectionItem.itemPosition
                )
                if (isItemClickable()) {
                    holder.itemView.setOnDebounceClick {
                        onItemClickedInternal.invoke(position)
                    }
                }
            }
            else -> {
                val headerItem = getItem(position) as SectionHeader<H>
                (holder as? BaseSectionHeaderViewHolder<H>)?.bind(headerItem.header)
            }
        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        when (holder) {
            is BaseSectionItemViewHolder<*> -> {
                val sectionItem = getItem(position) as SectionItem<T>
                if (payloads.contains(PAYLOAD_SELECTION)) {
                    holder.bindSelection(
                        selectedItemsMap[sectionItem.sectionId] == getInternalItem(
                            position
                        )
                    )
                }
                if (payloads.contains(PAYLOAD_ENABLE)) {
                    holder.bindEnable(
                        disableItems[sectionItem.sectionId]?.contains(
                            getInternalItem(position)
                        ) != true
                    )
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SectionHeader<*> -> VIEW_TYPE_HEADER
            else -> VIEW_TYPE_ITEM
        }
    }

    fun submit(sections: List<Section<H, T>>) {
        itemsList = mutableListOf()
        for (section in sections) {
            itemsList.add(section.header)
            itemsList.addAll(section.items)
        }
        super.submitList(itemsList)
    }

    // get id of T in String
    private fun getInternalItem(position: Int): T? {
        return (getItem(position) as? SectionItem<T>)?.item
    }

    open fun getSelectedItems(): List<T> {
        return selectedItemsMap.map { it.value }
    }

    open fun isItemSelectable(): Boolean = false

    open fun isItemClickable(): Boolean = false

    abstract fun createHeaderViewHolder(parent: ViewGroup): BaseSectionHeaderViewHolder<H>

    abstract fun createItemViewHolder(parent: ViewGroup): BaseSectionItemViewHolder<T>

    companion object {
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_ITEM = 2

        const val PAYLOAD_SELECTION = 1
        const val PAYLOAD_ENABLE = 2
    }
}