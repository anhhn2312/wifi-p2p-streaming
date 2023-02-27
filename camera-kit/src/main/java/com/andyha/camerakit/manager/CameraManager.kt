package com.andyha.camerakit.manager

import android.view.View
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.andyha.camerakit.analyzer.PreviewFrameAnalyzer
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
interface CameraManager {
    var containerView: View?
    var previewView: PreviewView?
    var lifecycleOwner: LifecycleOwner?

    var previewFrameAnalyzer: PreviewFrameAnalyzer?

    suspend fun initCamera()
    fun isCameraSwitchable(): Boolean
    fun switchCamera()
    fun onDestroy()
}
