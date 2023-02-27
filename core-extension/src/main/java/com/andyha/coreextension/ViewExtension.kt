package com.andyha.coreextension

import android.animation.Animator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.FontRes
import androidx.annotation.LayoutRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.andyha.coreextension.utils.DimensionUtils


fun ViewGroup.inflater(@LayoutRes id: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(this.context).inflate(id, this, attachToRoot)
}

fun ViewGroup.layoutInflater() = this.context.layoutInflater()

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.isShow(): Boolean = this.visibility == View.VISIBLE


fun View.isHide(): Boolean = (this.visibility == View.GONE || this.visibility == View.INVISIBLE)

fun View.toggleVisibility(onlyInvisibility: Boolean = false) {
    if (this.isVisible) {
        if (onlyInvisibility) {
            this.invisible()
        } else {
            this.hide()
        }
    } else {
        this.show()
    }
}

fun View.showCheck(isShow: Boolean) {
    if (isShow) {
        this.show()
    } else {
        this.hide()
    }
}

fun View.showExtra(isShow: Boolean) {
    if (isShow) {
        this.show()
    } else {
        this.invisible()
    }
}

fun View.toFloat(dipValue: Float): Float {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics)
}

fun View.hideKeyBoardWhenTouchOutside(viewFocus: View? = null, callback: (() -> Unit)? = null) {
    if (this !is EditText) {
        this.setOnTouchListener { _, _ ->
            hideKeyboard()
            viewFocus?.requestFocus()
            callback?.invoke()
            false
        }
    }
}


fun View.setPaddingTop(padding: Int) {
    setPaddingRelative(paddingLeft, padding, paddingRight, paddingBottom)
}

fun View.setPaddingBottom(padding: Int) {
    setPaddingRelative(paddingLeft, paddingTop, paddingRight, padding)
}

fun View.setOnGlobalLayoutListener(onGlobalLayout: () -> Unit) {
    this.viewTreeObserver.addOnGlobalLayoutListener(object :
        ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            onGlobalLayout.invoke()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}

fun Fragment.hideKeyBoardWhenTouchOutside() {
    view?.hideKeyBoardWhenTouchOutside()
}

fun EditText.setFont(@FontRes resId: Int) {
    val context = this.context
    this.typeface = context.getTypeFace(resId)
}

fun RecyclerView.Adapter<*>.observeData(event: () -> Unit) {
    registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            event.invoke()
            unregisterAdapterDataObserver(this)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            event.invoke()
            unregisterAdapterDataObserver(this)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            event.invoke()
            unregisterAdapterDataObserver(this)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            event.invoke()
            unregisterAdapterDataObserver(this)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            event.invoke()
            unregisterAdapterDataObserver(this)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            event.invoke()
            unregisterAdapterDataObserver(this)
        }
    })
}

fun EditText.getTextInputTrim(): String {
    return this.text.toString().trim()
}

fun EditText.setMaxLength(maxLength: Int) {
    filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
}

/**
 * Scale image
 * Extension method to scale image to fit with boundary
 *
 * @param bxInPx: width of boundary
 * @param byInPx: height of boundary
 * @param keepRatio: keep image ratio (width/height) or not
 */
@Throws(NoSuchElementException::class)
fun ImageView.scaleImage(bxInPx: Int, byInPx: Int, keepRatio: Boolean = true) {

    val result = try {
        this.drawable.scaleDrawable(bxInPx, byInPx, this.context, keepRatio)
    } catch (e: NoSuchElementException) {
        throw NoSuchElementException(e.message)
    }

    // Apply the scaled bitmap
    this.setImageDrawable(result)

    // Now change ImageView's dimensions to match the scaled image
    val params = this.layoutParams
    if ((result as? BitmapDrawable) != null) {
        params.width = result.bitmap.width
        params.height = result.bitmap.height
        this.layoutParams = params
    }
}

/**
 * Scale drawable
 * Extension method to scale drawable to fit with boundary
 *
 * @param bxInPx: width of boundary
 * @param byInPx: height of boundary
 * @param keepRatio: keep drawable ratio (width/height) or not
 */
@Throws(NoSuchElementException::class)
fun Drawable.scaleDrawable(
    bxInPx: Int,
    byInPx: Int,
    context: Context,
    keepRatio: Boolean = true
): Drawable {
    // Get bitmap from the Drawable.
    val bitmap: Bitmap = try {
        (this as? BitmapDrawable)?.bitmap
    } catch (e: NullPointerException) {
        throw NoSuchElementException("No drawable on given view")
    } catch (e: Exception) {
        throw NoSuchElementException("No drawable on given view by other errors")
    } ?: throw NoSuchElementException("Error when converting to bitmap")

    // Get current dimensions AND the desired bounding box
    val width: Int = try {
        bitmap.width
    } catch (e: NullPointerException) {
        throw NoSuchElementException("Can't find bitmap on given view/drawable")
    }
    val height = bitmap.height

    // Determine how much to scale: the dimension requiring less scaling is
    // closer to the its side. This way the image always stays inside your
    // bounding box AND either x/y axis touches it.
    val xScale = bxInPx.toFloat() / width
    val yScale = byInPx.toFloat() / height
    val scale = if (xScale <= yScale) xScale else yScale

    // Create a matrix for the scaling and add the scaling data
    val matrix = Matrix()
    if (keepRatio)
        matrix.postScale(scale, scale)
    else
        matrix.postScale(xScale, yScale)

    // Create a new bitmap and convert it to a format understood by the ImageView
    val scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)

    return BitmapDrawable(context.resources, scaledBitmap)
}


fun ImageView.setNewImageWithColorTint(imgRes: Int, imgColorTint: Int?) {
    var d: Drawable? =
        VectorDrawableCompat.create(resources, imgRes, null)
    if (d != null) {
        d = d.mutate()
        d = DrawableCompat.wrap(d)
        if (imgColorTint != null) DrawableCompat.setTint(
            d,
            ContextCompat.getColor(context, imgColorTint)
        )
        this.setImageDrawable(d)
    }
}

fun LinearLayoutManager.smoothScrollAction(context: Context, index: Int, speedPixel: Float) {
    if (index >= 0) {
        this.startSmoothScroll(object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float = speedPixel
        }.apply { targetPosition = index })
    }
}

/**
 * To Bitmap
 * Extension method to get the bitmap used by this drawable to render.
 *
 */
fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return this.bitmap
    }

    // We ask for the bounds if they have been set as they would be most
    // correct, then we check we are  > 0
    val width: Int = if (!this.bounds.isEmpty) this.bounds.width() else this.intrinsicWidth
    val height: Int = if (!this.bounds.isEmpty) this.bounds.height() else this.intrinsicHeight

    // Now we check we are > 0
    val bitmap = Bitmap.createBitmap(
        if (width <= 0) 1 else width,
        if (height <= 0) 1 else height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)

    return bitmap
}


fun View?.expandClickArea(
    left: Float = 12F,
    top: Float = 12F,
    right: Float = 12F,
    bottom: Float = 12F
) {
    if (this == null || this.parent == null || this.context == null) {
        return
    }
    val parent = this.parent as View // button: the view you want to enlarge hit area
    parent.post {
        val rect = Rect()
        this.getHitRect(rect)
        rect.left -= DimensionUtils.dpToPx(left).toInt() // increase left hit area
        rect.top -= DimensionUtils.dpToPx(top).toInt() // increase top hit area
        rect.bottom += DimensionUtils.dpToPx(bottom).toInt() // increase bottom hit area
        rect.right += DimensionUtils.dpToPx(right).toInt() // increase right hit area
        parent.touchDelegate = TouchDelegate(rect, this)
    }
}

fun View.setbackgroundAttribute(attr: Int) {
    val outValue = TypedValue()
    context.theme.resolveAttribute(attr, outValue, true)
    background = AppCompatResources.getDrawable(context, outValue.resourceId)
}

inline fun ViewPropertyAnimator.addListener(
    crossinline onEnd: (animator: Animator) -> Unit = {},
    crossinline onStart: (animator: Animator) -> Unit = {},
    crossinline onCancel: (animator: Animator) -> Unit = {},
    crossinline onRepeat: (animator: Animator) -> Unit = {}
): ViewPropertyAnimator {
    val listener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animator: Animator) = onRepeat(animator)
        override fun onAnimationEnd(animator: Animator) = onEnd(animator)
        override fun onAnimationCancel(animator: Animator) = onCancel(animator)
        override fun onAnimationStart(animator: Animator) = onStart(animator)
    }

    setListener(listener)
    return this
}

fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id).setOnClickListener(listener)
    }
}