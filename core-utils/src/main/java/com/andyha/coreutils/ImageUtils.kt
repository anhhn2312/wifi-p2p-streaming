package com.andyha.coreutils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import com.andyha.corenetwork.config.NetworkConfigConstants
import com.andyha.coreutils.time.TimeFormatter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.Rotate
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import timber.log.Timber
import java.io.File
import java.util.*


object ImageUtils {

    private val TAG = ImageUtils::class.java.simpleName

    fun loadImage(imageView: ImageView, imageUrl: String?) {
        if (imageUrl == null) return
        Glide.with(imageView.context)
            .load(imageUrl)
            .into(imageView)
    }

    fun loadImage(imageView: ImageView, uri: Uri?) {
        if (uri == null) return
        Glide.with(imageView.context)
            .load(uri)
            .into(imageView)
    }

    fun loadImage(imageView: ImageView, resId: Int) {
        Glide.with(imageView.context)
            .load(resId)
            .into(imageView)
    }

    fun loadImage(imageView: ImageView, resId: Int, placeHolder: Int) {
        Glide.with(imageView.context)
            .load(resId)
            .apply(RequestOptions().placeholder(placeHolder))
            .into(imageView)
    }

    fun loadImage(imageView: ImageView, imageUrl: String?, placeHolder: Int) {
        if (imageUrl == null) return
        Glide.with(imageView.context)
            .load(imageUrl)
            .apply(RequestOptions().placeholder(placeHolder))
            .into(imageView)
    }

    fun loadImage(
        imageView: ImageView,
        imageUrl: String?,
        placeHolder: Int,
        onResultLoading: ((Boolean) -> Unit)? = null,
    ) {
        if (imageUrl == null) {
            imageView.setImageResource(placeHolder)
            return
        }

        Glide.with(imageView.context)
            .load(imageUrl)
            .apply(RequestOptions().placeholder(placeHolder).error(placeHolder))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onResultLoading?.invoke(false)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onResultLoading?.invoke(true)
                    return false
                }
            }).into(imageView)
    }

    fun loadImage(imageView: ImageView, imageUrl: String?, placeHolder: Int, fallbackId: Int) {
        if (imageUrl == null) return
        Glide.with(imageView.context)
            .load(imageUrl)
            .apply(RequestOptions().placeholder(placeHolder).error(fallbackId))
            .into(imageView)
    }

    fun loadImage(imageView: ImageView, imageUrl: String?, fallbackImageUrl: String) {
        if (imageUrl == null) return
        Glide.with(imageView.context)
            .load(imageUrl)
            .error(Glide.with(imageView.context).load(fallbackImageUrl))
            .into(imageView)
    }

    fun createBitmapFromUrl(context: Context, url: String, width: Int, height: Int): Bitmap? {
        try {
            return Glide.with(context)
                .asBitmap()
                .load(url)
                .submit(width, height)
                .get()
        } catch (e: Exception) {
            return null
        }
    }

    fun createBitmapFromUrl(context: Context, url: String): Bitmap? {
        try {
            return Glide.with(context)
                .asBitmap()
                .load(url)
                .submit()
                .get()
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Create uri save image when trim image with path save image of app
     */
    fun createUriToMakeImage(
        suffix: String,
        context: Context,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): Uri? {
        val currentTimeMillis = System.currentTimeMillis()
        val today = Date(currentTimeMillis)
        val dateFormat = TimeFormatter.getDateFormat("yyyyMMdd-HH:mm")
        val title = dateFormat.format(today)
        val file = createImageFile(suffix)
        var uri: Uri? = null
        if (file != null) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, title)
            values.put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            values.put(MediaStore.Images.Media.DATA, file.path)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(format))

            val time = currentTimeMillis / 1000
            values.put(MediaStore.MediaColumns.DATE_ADDED, time)
            values.put(MediaStore.MediaColumns.DATE_MODIFIED, time)
            if (file.exists()) {
                values.put(MediaStore.Images.Media.SIZE, file.length())
            }
            val resolver = context.contentResolver
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }
        Timber.d("createUriToMakeImage: $uri")
        return uri
    }

    /**
     * Get the path for saving the image
     * @param format format of image
     * @param suffix suffix of file name
     * @return [File]
     */
    fun createImageFile(
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        suffix: String? = null
    ): File? {
        val currentTimeMillis = System.currentTimeMillis()
        val today = Date(currentTimeMillis)
        val dateFormat = TimeFormatter.getDateFormat("yyyyMMdd_HHmmss")
        val title = dateFormat.format(today)
        val fileNameFull = if (!suffix.isNullOrEmpty()) {
            "$title$suffix.${getMimeType(format)}"
        } else {
            "$title.${getMimeType(format)}"
        }
        return getOutputImageFile(fileName = fileNameFull)
    }

    fun createImageFile(): File? {
        return createImageFile(format = Bitmap.CompressFormat.JPEG, suffix = null)
    }

    fun createImageFile(suffix: String? = null): File? {
        return createImageFile(format = Bitmap.CompressFormat.JPEG, suffix = suffix)
    }

    /**
     * getMimeType CompressFormat for image
     * @param format: compress format
     * return string mineType: jpeg, png
     */
    private fun getMimeType(format: Bitmap.CompressFormat): String {
        return when (format) {
            Bitmap.CompressFormat.JPEG -> "jpeg"
            Bitmap.CompressFormat.PNG -> "png"
            else -> "png"
        }
    }

    /**
     * Get the path for saving the image
     *
     * @param fileName
     * @return [File]
     */
    fun getOutputImageFile(fileName: String, suffix: String? = null): File? {
        var nameFileFull = fileName
        if (suffix != null) {
            nameFileFull = "$fileName$suffix"
        }
        return getOutputFile(Environment.DIRECTORY_PICTURES, nameFileFull)
    }


    fun getOutputFile(type: String, fileName: String): File? {
        var file: File? = null
        // We can read and write media, Check externalStorage available
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            var dir: File? = File(
                Environment.getExternalStoragePublicDirectory(type),
                "DIR_OUTPUT"
            )
            if (dir != null) {
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        Timber.tag(TAG).d(
                            "#getOutputFile: mkdirs() failed. path=%s",
                            dir.absolutePath
                        )
                        dir = null
                    }
                }
                if (dir != null) {
                    file = File(dir.path + File.separator + fileName)
                }
            }
        }
        return file
    }

    fun loadImageWidthRound(
        imageView: ImageView,
        imageUrl: String?,
        placeHolder: Int? = null,
        radius: Int = 25,
        onResultLoading: ((Boolean) -> Unit)? = null,
    ) {
        val requestManager = Glide.with(imageView.context)
            .load(imageUrl)
            .transform(
                RoundedCornersTransformation(
                    radius,
                    0,
                    RoundedCornersTransformation.CornerType.ALL,
                    RoundedCornersTransformation.CrossCutType.NONE,
                    15
                )
            )
            .override(imageView.width, imageView.height)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onResultLoading?.invoke(false)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onResultLoading?.invoke(true)
                    return false
                }

            })

        if (placeHolder != null) {
            requestManager.apply(RequestOptions().placeholder(placeHolder).error(placeHolder))
        }
        requestManager.into(imageView)

    }

    /**
     * Load image circle
     *
     * @param imageView
     * @param imageUrl
     * @param placeHolder
     */
    fun loadImageCircle(
        imageView: ImageView,
        imageUrl: String?,
        placeHolder: Int,
        @Nullable onResultLoading: ((Boolean) -> Unit)? = null,
    ) {
        val requestManager = Glide.with(imageView.context)
            .load(imageUrl)
            .circleCrop()
            .apply(RequestOptions().placeholder(placeHolder))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onResultLoading?.invoke(false)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onResultLoading?.invoke(true)
                    return false
                }

            })
        requestManager.into(imageView)
    }

    /**
     * Load image circle with size:  Fix error: Load failed for...with size [HHxWW]...
     *
     * @param imageView
     * @param imageUrl
     * @param placeHolder
     */
    fun loadImageCircleWithSize(imageView: ImageView, imageUrl: String?, placeHolder: Int) {
        if (imageUrl == null) return
        Glide.with(imageView.context)
            .load(imageUrl)
            .circleCrop()
            .override(
                imageView.width,
                imageView.height
            ) // Fix error: Load failed for...with size [84x84]...
            .apply(RequestOptions().placeholder(placeHolder))
            .into(imageView)
    }

    fun loadRotateImage(iv: ImageView, resId: Int, degrees: Int) {
        Glide.with(iv.context)
            .load(resId)
            .transform(Rotate(degrees))
            .into(iv)
    }

    private fun getGlideUrl(accessToken: String? = null, src: String): GlideUrl? {
        var glideUrl: GlideUrl? = null
        accessToken?.let {
            if (URLUtil.isValidUrl(src)) {
                glideUrl = GlideUrl(
                    src,
                    LazyHeaders.Builder()
                        .addHeader(
                            NetworkConfigConstants.AUTHORIZATION,
                            "${NetworkConfigConstants.BEARER} $accessToken"
                        )
                        .build()
                )
            }
        }
        return glideUrl
    }

    fun ImageView.loadImageListener(
        src: String,
        accessToken: String? = null,
        onResultLoading: (Boolean) -> Unit
    ) {
        val glideUrl = getGlideUrl(accessToken, src)

        Glide
            .with(this)
            .load(glideUrl ?: src)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onResultLoading.invoke(false)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onResultLoading.invoke(true)
                    return false
                }

            })
            .into(this)
    }

    fun View.loadBackground(@DrawableRes resId: Int) {
        Glide
            .with(this)
            .load(resId)
            .into(object : CustomViewTarget<View, Drawable>(this) {
                override fun onLoadFailed(errorDrawable: Drawable?) {

                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    this@loadBackground.background = resource
                }

                override fun onResourceCleared(placeholder: Drawable?) {

                }
            })
    }
}