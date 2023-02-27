package com.andyha.p2p.streaming.kit.encoder

import android.media.MediaFormat
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.annotation.RequiresApi
import java.io.File

open class MuxerConfig @JvmOverloads constructor(
    var file: FileOrParcelFileDescriptor,
    var videoWidth: Int = 1080,
    var videoHeight: Int = 720,
    var mimeType: String = MediaFormat.MIMETYPE_VIDEO_AVC,
    var framesPerImage: Int = 1,
    var framesPerSecond: Float = 30F,
    var bitrate: Int = 10000000,
    var frameMuxer: FrameMuxer = Mp4FrameMuxer(file, framesPerSecond),
    var iFrameInterval: Int = 10
){
    @JvmOverloads constructor(
        file: File,
        videoWidth: Int = 1080,
        videoHeight: Int = 720,
        mimeType: String = MediaFormat.MIMETYPE_VIDEO_AVC,
        framesPerImage: Int = 1,
        framesPerSecond: Float = 30F,
        bitrate: Int = 10000000,
        frameMuxer: FrameMuxer = Mp4FrameMuxer(file.absolutePath, framesPerSecond),
        iFrameInterval: Int = 10
    ) : this(
        FileOrParcelFileDescriptor(file),
            videoWidth,
            videoHeight,
            mimeType, framesPerImage, framesPerSecond, bitrate, frameMuxer, iFrameInterval)
}

interface MuxingCompletionListener {
    fun onVideoSuccessful(file: FileOrParcelFileDescriptor)
    fun onVideoError(error: Throwable)
}


/**
 * Mixture of File and ParcelFileDescriptor supposed to be a compatibility class to make it possible to use this
 * Library on devices with Android 10+, because you cant work with Files on Android 10+ anymore
 * (You don't have access to the media storage via Files).
 * If this Class is an ParcelFileDescriptor it is not supposed to use this as a File, because it may occur that this
 * File won't work because it is not initialised correctly.
 * Don't close yourParcelFileDescriptor while using this class.
 */
class FileOrParcelFileDescriptor : File {
    constructor(s:String): super(s)
    constructor(file:File): super(file.path)
    @RequiresApi(Build.VERSION_CODES.O) constructor(parcelFileDescriptor:ParcelFileDescriptor): super(parcelFileDescriptor.toString()){
        this.parcelFileDescriptor=parcelFileDescriptor
        this.isParcelFileDescriptor=true
    }
    var isParcelFileDescriptor:Boolean=false
        private set
    var parcelFileDescriptor: ParcelFileDescriptor?=null
        @RequiresApi(Build.VERSION_CODES.O)
        set(value) {
            field=value
            isParcelFileDescriptor = field != null
        }
}