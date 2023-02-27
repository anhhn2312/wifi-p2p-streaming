package com.andyha.coreextension

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ListAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


fun Any.getSimpleName() = this::class.java.simpleName

val <T : Any?> T.TAG: String
    get() = (this as? Any)?.let { javaClass.declaringClass?.simpleName ?: javaClass.simpleName } ?: "null"

fun Any.toJson() = Gson().toJson(this) ?: "null"

fun Boolean.isTrue(body: (() -> Unit)?): Boolean {
    if (this) body?.invoke()
    return this
}

fun Boolean.isFalse(body: (() -> Unit)?): Boolean {
    if (!this) body?.invoke()
    return this
}

fun Long.convertTimestampToDateSpace(): String {
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return dateFormat.format(Date(this))
}

fun getDateByDays(days: Int): String {
    val cal = Calendar.getInstance()
    val s = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
    cal.add(Calendar.DAY_OF_YEAR, days)
    return s.format(Date(cal.timeInMillis))
}


fun ListAdapter.measureContentWidth(context: Context): Int {
    var mMeasureParent: ViewGroup? = null
    var maxWidth = 0
    var itemView: View? = null
    var itemType = 0
    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    val count: Int = this.count
    for (i in 0 until count) {
        val positionType: Int = this.getItemViewType(i)
        if (positionType != itemType) {
            itemType = positionType
            itemView = null
        }
        if (mMeasureParent == null) {
            mMeasureParent = FrameLayout(context)
        }
        itemView = this.getView(i, itemView, mMeasureParent)
        itemView.measure(widthMeasureSpec, heightMeasureSpec)
        val itemWidth = itemView.measuredWidth
        if (itemWidth > maxWidth) {
            maxWidth = itemWidth
        }
    }
    return maxWidth
}

fun List<File>.makeBodyFromListFile(parameter: String, defaultExtension: String? = null): RequestBody {
    val bodyData = MultipartBody.Builder().setType(MultipartBody.FORM)
    this.forEach { file ->
        Timber.d("upload image: ${file.path}")
        var extension = ".jpg"
        if (defaultExtension == null) {
            val index: Int = file.name.lastIndexOf('.')
            if (index > 0) {
                extension = file.name.substring(index)
            }
        } else {
            extension = defaultExtension
        }
        bodyData.addFormDataPart(
            parameter, "android_${System.currentTimeMillis()}$extension",
            file.asRequestBody()
        )
    }
    return bodyData.build()
}

fun Uri.getImagePath(context: Context): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor =
        this.let { context.contentResolver.query(this, projection, null, null, null) }
            ?: return null
    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    val s = cursor.getString(columnIndex)
    cursor.close()
    return s
}

fun <T> List<T>.stringFromListToJson(): String {
    return try {
        val listType: Type = object : TypeToken<ArrayList<T>>() {}.type
        Gson().toJson(this, listType)
    } catch (e: Exception) {
        ""
    }
}

fun <T> List<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this)
}

fun <T> List<T>.getItem(index: Int): T?{
    if(index < 0 || index >= size) return null
    return this[index]
}