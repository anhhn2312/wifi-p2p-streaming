package com.andyha.coreui.base.ui.widget.tagview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import com.andyha.coreui.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class TagView : ChipGroup {

    private lateinit var mTags: ArrayList<Tag>
    private lateinit var mSelectedTags: MutableMap<Tag, Boolean>

    private lateinit var mOnCheckedChangeListener: CompoundButton.OnCheckedChangeListener

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        mTags = ArrayList()
        mSelectedTags = mutableMapOf()
        mOnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, _ ->
            if (isSingleSelection) {
                onSingleCheck(buttonView as Chip)
            } else {
                onMultipleCheck(buttonView as Chip)
            }
        }
    }

    private fun onSingleCheck(chip: Chip) {
        val tag = chip.tag as Tag
        if (mSelectedTags.containsKey(tag)) {
            mSelectedTags.clear()
        } else {
            mSelectedTags.clear()
            mSelectedTags[tag] = true
        }
    }

    private fun onMultipleCheck(chip: Chip) {
        val tag = chip.tag as Tag
        if (mSelectedTags.containsKey(tag)) {
            mSelectedTags.remove(tag)
        } else {
            mSelectedTags[tag] = true
        }
    }

    fun setTags(tags: List<Tag>) {
        mTags.clear()
        mTags.addAll(tags)

        removeAllViews()
        val layoutInflater = LayoutInflater.from(context)
        tags.forEach { tag ->
            val chip =
                (layoutInflater.inflate(R.layout.item_tag_view, this, false) as Chip).apply {
                    this.id = View.generateViewId()
                    this.tag = tag
                    this.text = tag.value
                    this.setOnCheckedChangeListener(mOnCheckedChangeListener)
                }
            this.addView(chip)
        }
    }

    fun setSelectedTags(tags: List<Tag>?) {
        if (mTags.isEmpty()) {
            return
        }

        mSelectedTags.clear()
        tags?.forEach {
            mSelectedTags[it] = true
        }

        for (i in 0 until childCount) {
            val chip = getChildAt(i) as Chip
            val tag = chip.tag
            chip.setOnCheckedChangeListener(null)
            chip.isChecked = mSelectedTags.containsKey(tag)
            chip.setOnCheckedChangeListener(mOnCheckedChangeListener)
        }
    }

    fun setSelected(index: Int){
        if(childCount > index){
            val chip = getChildAt(index) as Chip
            chip.isChecked = true
        }
    }

    fun getSelectedTags(): List<Tag> {
        return mSelectedTags.keys.toList()
    }
}