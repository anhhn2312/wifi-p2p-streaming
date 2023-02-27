package com.andyha.coreutils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Environment
import android.text.TextUtils
import androidx.annotation.Nullable
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


object FileUtils {

    //region resize image
    fun File.resizeImageFile(
        context: Context,
        @Nullable requiredSize: Int = 95,
        @Nullable qualityValue: Int = 80,
        autoRotate: Boolean = true
    ): File? {
        return try {

            // Just decode bounds to know actually size of the image
            val bitmapOption = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeFile(this@resizeImageFile.path, this)
            }

            // Calculate inSampleSize & decode file to bitmap
            val requiredWidth = requiredSize * bitmapOption.outWidth
            val requiredHeight = requiredSize * bitmapOption.outHeight
            val inSampleSize = calculateInSampleSize(bitmapOption, requiredWidth, requiredHeight)
            bitmapOption.apply {
                this.inJustDecodeBounds = false
                this.inSampleSize = inSampleSize
            }
            var bitmap =
                BitmapFactory.decodeFile(this@resizeImageFile.path, bitmapOption) ?: return null

            // Rotate bitmap if it needed
            if (autoRotate) {
                val rotatedImage = rotateImage(this@resizeImageFile, bitmap)
                if (rotatedImage !== bitmap) {
                    bitmap.recycle()
                    bitmap = rotatedImage!!
                }
            }

            // Create temp file
            val prefix = "img_sample_" + System.currentTimeMillis()
            val suffix = ".jpg"
            val photoFile = createTempFile(context, prefix, suffix, Environment.DIRECTORY_PICTURES)

            // Compress bitmap to file
            compressImage(photoFile, bitmap, qualityValue)
            return photoFile
        } catch (e: Exception) {
            Timber.d("compress image error ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * Find the correct scale value. It should be the power of 2.
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun createTempFile(context: Context, prefix: String, suffix: String, directory: String): File {
        // Create a new file, overwrite old file if is existed before
        val photoFile = File.createTempFile(
            prefix,
            suffix,
            context.getExternalFilesDir(directory)
        )
        if (photoFile.exists()) {
            photoFile.createNewFile()
        }
        return photoFile
    }


    private fun rotateImage(file: File, bitmap: Bitmap): Bitmap? {
        val exif = ExifInterface(file.path)
        val orientation: Int = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        var degrees = 0f
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> degrees = 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> degrees = 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> degrees = 270f
        }

        val matrix = Matrix().apply {
            postRotate(degrees)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun compressImage(photoFile: File, bitmap: Bitmap, qualityValue: Int) {
        val outputStream = FileOutputStream(photoFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, qualityValue, outputStream)
        outputStream.close()
        Timber.d("compress image ${photoFile.path}")
    }
    //endregion resize image

    fun getAppDataFile(context: Context, filename: String): File {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val filePath = "$directory/$filename"
        return File(filePath)
    }

    fun saveResponseBodyToDisk(context: Context, body: ResponseBody, fileName: String): File? {
        try {
            val file = getAppDataFile(context, fileName) ?: return null
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                inputStream = body.byteStream()
                outputStream = FileOutputStream(file)
                val data = ByteArray(4096)
                var count: Int
                var progress = 0
                val fileSize = body.contentLength()
                Timber.d("File Size=$fileSize")
                while (inputStream.read(data).also { count = it } != -1) {
                    outputStream.write(data, 0, count)
                    progress += count
                    Timber.d(
                        "%s%s",
                        "Progress: " + progress + "/" + fileSize + " >>>> ",
                        progress.toFloat() / fileSize
                    )
                }
                outputStream.flush()
                Timber.d(file.parent)
                val pairs = Pair(100, 100L)
                return file
            } catch (e: IOException) {
                e.printStackTrace()
                Timber.d("Failed to save the file!")
                return null
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Timber.d("Failed to save the file!")
            return null
        }
    }


    fun checkSum(md5: String, updateFile: File?): Boolean {
        if (TextUtils.isEmpty(md5) || updateFile == null) {
            Timber.e("MD5 string empty or updateFile null")
            return false
        }
        val calculatedDigest = calculateMD5(updateFile)
        if (calculatedDigest == null) {
            Timber.e("calculatedDigest null")
            return false
        }
        Timber.v("Calculated digest: $calculatedDigest")
        Timber.v("Provided digest: $md5")
        return calculatedDigest.equals(md5, ignoreCase = true)
    }

    fun calculateMD5(updateFile: File?): String? {
        val digest: MessageDigest = try {
            MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            Timber.e("Exception while getting digest: $e")
            return null
        }
        val inputStream: InputStream = try {
            FileInputStream(updateFile)
        } catch (e: FileNotFoundException) {
            Timber.e("Exception while getting FileInputStream: $e")
            return null
        }
        val buffer = ByteArray(8192)
        var read: Int
        return try {
            while (inputStream.read(buffer).also { read = it } > 0) {
                digest.update(buffer, 0, read)
            }
            val md5sum: ByteArray = digest.digest()
            val bigInt = BigInteger(1, md5sum)
            var output: String = bigInt.toString(16)
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0')
            output
        } catch (e: IOException) {
            throw RuntimeException("Unable to process file for MD5", e)
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                Timber.e("Exception on closing MD5 input stream: $e")
            }
        }
    }

    fun getFileInPublicExternalStorage(
        publicDir: String,
        folderName: String? = null,
        fileName: String
    ): File {
        val folder = File(Environment.getExternalStoragePublicDirectory(publicDir), folderName)
        // Create the directory if it does not exist
        if (!folder.exists()) folder.mkdirs()
        Timber.d("Got folder at: $folder.absolutePath")
        val file = File(folder, fileName)
        Timber.d("Got file at: $file.absolutePath")
        return file
    }

    fun bitmapToByteArray(inbitmap: Bitmap, scale: Int): ByteArray {
        val bitmap = Bitmap.createScaledBitmap(
            inbitmap, inbitmap.width / scale,
            inbitmap.height / scale, false
        )
        val buffer = ByteArrayOutputStream(bitmap.width * bitmap.height)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, buffer)
        return buffer.toByteArray()
    }
}