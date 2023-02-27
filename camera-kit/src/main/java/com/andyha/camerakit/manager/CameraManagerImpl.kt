package com.andyha.camerakit.manager

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
import androidx.lifecycle.LifecycleOwner
import androidx.window.WindowManager
import com.andyha.camerakit.analyzer.PreviewFrameAnalyzer
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@FragmentScoped
class CameraManagerImpl @Inject constructor(
    @ActivityContext private val context: Context
) : CameraManager {

    override var containerView: View? = null
    override var previewView: PreviewView? = null
    override var lifecycleOwner: LifecycleOwner? = null

    private var displayId: Int? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private val windowManager: WindowManager by lazy { WindowManager(context) }
    private val displayManager: DisplayManager by lazy {
        context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    private val cameraExecutor: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
    }

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) {
            if (displayId == this@CameraManagerImpl.displayId) {
                imageCapture?.targetRotation = containerView?.display?.rotation ?: 0
                imageAnalyzer?.targetRotation = containerView?.display?.rotation ?: 0
            }
        }
    }

    override var previewFrameAnalyzer: PreviewFrameAnalyzer? = null

    override suspend fun initCamera() {
        displayManager.registerDisplayListener(displayListener, null)
        // Keep track of the display in which this view is attached
        displayId = previewView?.display?.displayId
        cameraProvider = ProcessCameraProvider.getInstance(context).await()

        // Select lensFacing depending on the available cameras
        lensFacing = when {
            hasBackCamera() -> CameraSelector.LENS_FACING_BACK
            hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
            else -> throw IllegalStateException("Back and front camera are unavailable")
        }
        bindCameraUseCases()
    }

    private fun bindCameraUseCases() {

        // Get screen metrics used to setup camera for full screen resolution
        val metrics = windowManager.getCurrentWindowMetrics().bounds
        Timber.tag(TAG).d("Screen metrics: ${metrics.width()} x ${metrics.height()}")

        val screenAspectRatio = aspectRatio(metrics.width(), metrics.height())
        Timber.tag(TAG).d("Preview aspect ratio: $screenAspectRatio")

        val rotation = previewView?.display?.rotation

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        // Preview
        preview = Preview.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(screenAspectRatio)
//            .setTargetResolution(Size(1080, 1920))
            .setTargetRotation(rotation ?: 0)
            .build()

        // ImageCapture
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            // We request aspect ratio but no resolution to match preview config, but letting
            // CameraX optimize for whatever specific resolution best fits our use cases
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(rotation ?: 0)
            .build()

        // ImageAnalysis
        imageAnalyzer = ImageAnalysis.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(rotation ?: 0)
//            .setTargetResolution(Size(1080, 1920))
            .build()
            // The analyzer can then be assigned to the instance
            .also {
                previewFrameAnalyzer = PreviewFrameAnalyzer()
                it.setAnalyzer(cameraExecutor, previewFrameAnalyzer!!)
            }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        if (camera != null) {
            // Must remove observers from the previous camera instance
            removeCameraStateObservers(camera!!.cameraInfo)
        }

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = lifecycleOwner?.let {
                cameraProvider.bindToLifecycle(
                    it, cameraSelector, preview, imageCapture, imageAnalyzer
                )
            }

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(previewView?.surfaceProvider)
            camera?.cameraInfo?.let { observeCameraState(it) }
        } catch (exc: Exception) {
            Timber.e("Use case binding failed")
        }
    }

    private fun removeCameraStateObservers(cameraInfo: CameraInfo) {
        lifecycleOwner?.let { cameraInfo.cameraState.removeObservers(it) }
    }

    private fun observeCameraState(cameraInfo: CameraInfo) {
        lifecycleOwner?.let {
            cameraInfo.cameraState.observe(it) { cameraState ->
                run {
                    when (cameraState.type) {
                        CameraState.Type.PENDING_OPEN -> {
                            // Ask the user to close other camera apps
                            Timber.d("CameraState: Pending Open")
                        }
                        CameraState.Type.OPENING -> {
                            // Show the Camera UI
                            Timber.d("CameraState: Opening")
                        }
                        CameraState.Type.OPEN -> {
                            // Setup Camera resources and begin processing
                            Timber.d("CameraState: Open")
                        }
                        CameraState.Type.CLOSING -> {
                            // Close camera UI
                            Timber.d("CameraState: Closing")
                        }
                        CameraState.Type.CLOSED -> {
                            // Free camera resources
                            Timber.d("CameraState: Closed")
                        }
                    }
                }

                cameraState.error?.let { error ->
                    when (error.code) {
                        // Open errors
                        CameraState.ERROR_STREAM_CONFIG -> {
                            // Make sure to setup the use cases properly
                            Timber.d("Stream config error")
                        }
                        // Opening errors
                        CameraState.ERROR_CAMERA_IN_USE -> {
                            // Close the camera or ask user to close another camera app that's using the
                            // camera
                            Timber.d("Camera in use")
                        }
                        CameraState.ERROR_MAX_CAMERAS_IN_USE -> {
                            // Close another open camera in the app, or ask the user to close another
                            // camera app that's using the camera
                            Timber.d("Max cameras in use")
                        }
                        CameraState.ERROR_OTHER_RECOVERABLE_ERROR -> {
                            Timber.d("Other recoverable error")
                        }
                        // Closing errors
                        CameraState.ERROR_CAMERA_DISABLED -> {
                            // Ask the user to enable the device's cameras
                            Timber.d("Camera disabled")
                        }
                        CameraState.ERROR_CAMERA_FATAL_ERROR -> {
                            // Ask the user to reboot the device to restore camera function
                            Timber.d("Fatal error")
                        }
                        // Closed errors
                        CameraState.ERROR_DO_NOT_DISTURB_MODE_ENABLED -> {
                            // Ask the user to disable the "Do Not Disturb" mode, then reopen the camera
                            Timber.d("Do not disturb mode enabled")
                        }
                    }
                }
            }
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }


    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }

    override fun isCameraSwitchable(): Boolean {
        return hasBackCamera() && hasFrontCamera()
    }

    override fun switchCamera() {
        lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }
        // Re-bind use cases to update selected camera
        bindCameraUseCases()
    }

    override fun onDestroy() {
        // Shut down our background executor
        cameraExecutor.shutdown()
        displayManager.unregisterDisplayListener(displayListener)
        lifecycleOwner = null
        containerView = null
        previewView = null
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_TYPE = "image/jpeg"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}