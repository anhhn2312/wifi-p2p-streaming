package com.andyha.coreui.base.ui.widget

import android.content.Context
import android.view.Gravity.BOTTOM
import android.view.Gravity.FILL_HORIZONTAL
import android.view.LayoutInflater
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import com.andyha.coreextension.dimen
import com.andyha.coreui.R
import com.andyha.coreui.databinding.BaseToastBinding


class BaseToast(context: Context) : Toast(context) {

    companion object {

        @JvmStatic
        internal fun showToast(
            context: Context,
            message: String,
            duration: Int = LENGTH_SHORT
        ) {
            val viewBinding = BaseToastBinding.inflate(LayoutInflater.from(context), null, false)
            viewBinding.apply {
                tvMessage.text = message
            }

            Toast(context).apply {
                this.duration = duration
                setGravity(BOTTOM or FILL_HORIZONTAL, 0, context.dimen(R.dimen.dimen24dp))
                view = viewBinding.root
                show()
            }
        }
    }
}


// --- Extension functions ---

fun Context?.showToast(message: String, shortLength: Boolean = true) {
    this?.let {
        BaseToast.showToast(
            it,
            message = message,
            duration = if (shortLength) LENGTH_SHORT else LENGTH_LONG,
        )
    }
}

fun Context?.showToast(messageResId: Int, shortLength: Boolean = true) {
    this?.let {
        BaseToast.showToast(
            it,
            message = getString(messageResId),
            duration = if (shortLength) LENGTH_SHORT else LENGTH_LONG,
        )
    }
}