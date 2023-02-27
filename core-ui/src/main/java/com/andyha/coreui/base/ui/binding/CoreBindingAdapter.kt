package com.andyha.coreui.base.ui.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.andyha.coreui.base.ui.widget.textview.BaseTextView
import com.andyha.coreutils.ImageUtils


@BindingAdapter(value = ["url"])
fun setImageUrl(imageView: ImageView, url: String?) {
    return ImageUtils.loadImage(imageView, url)
}

@BindingAdapter(value=["textRes"])
fun setText(textView: BaseTextView, stringRes: Int){
    textView.setCustomText(stringRes)
}
