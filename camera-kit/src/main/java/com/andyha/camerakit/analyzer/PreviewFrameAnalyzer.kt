package com.andyha.camerakit.analyzer

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.andyha.camerakit.utils.BitmapUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import java.nio.ByteBuffer

class PreviewFrameAnalyzer : ImageAnalysis.Analyzer {

    private val _output: MutableSharedFlow<Bitmap> = MutableSharedFlow(replay = 1)
    val output: SharedFlow<Bitmap> = _output.asSharedFlow()

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }

    override fun analyze(image: ImageProxy) {
        val bitmap = BitmapUtils.getBitmap(image)
        Timber.d("Analyze: after getBitmap: ${bitmap?.width} - ${bitmap?.height}")
        bitmap?.let { bitmap -> _output.tryEmit(bitmap) }
        image.close()
    }
}