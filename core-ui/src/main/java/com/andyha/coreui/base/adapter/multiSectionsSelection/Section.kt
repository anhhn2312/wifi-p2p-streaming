package com.andyha.coreui.base.adapter.multiSectionsSelection

import com.andyha.coreui.R


data class Section<H: Any, T> private constructor(
    var header: SectionHeader<H>,
    var items: List<SectionItem<T>>,
) {
    constructor(header: H, items: List<T>) : this(
        SectionHeader(header), items.map {
            SectionItem(header, it).apply { setPosition(items.size, items.indexOf(it)) }
        }
    )
}

data class SectionHeader<H>(
    var header: H
)

data class SectionItem<T>(
    var sectionId: Any,
    var item: T,
    var itemPosition: ItemPosition = ItemPosition.Alone,
    var selected: Boolean = false
) {
    fun setPosition(listSize: Int, position: Int) {
        if (listSize == 1) {
            itemPosition = ItemPosition.Alone
            return
        }
        if (position == 0) {
            itemPosition = ItemPosition.Start
            return
        }
        if (position == listSize - 1) {
            itemPosition = ItemPosition.End
            return
        }
        itemPosition = ItemPosition.Middle
    }
}

enum class ItemPosition(val background: Int, val dividerEnabled: Boolean) {
    Start(R.drawable.default_top_item_background, true),
    Middle(R.drawable.default_middle_item_background, true),
    End(R.drawable.default_bottom_item_background, false),
    Alone(R.drawable.default_full_item_background, false)
}