package com.andyha.p2p.streaming.kit.encoder

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.annotation.RequiresApi
import timber.log.Timber
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit


class Mp4FrameMuxer(private val muxer: MediaMuxer, private val fps: Float) : FrameMuxer {

    constructor(fileOrParcelFileDescriptor: FileOrParcelFileDescriptor, fps: Float) : this(
        forceOpenMediaMuxer(fileOrParcelFileDescriptor),fps){
        parcelFileDescriptor=fileOrParcelFileDescriptor.parcelFileDescriptor
    }
    constructor(path: String, fps: Float): this(MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4),fps)

    companion object {
        private val TAG: String = Mp4FrameMuxer::class.java.simpleName
        fun forceOpenMediaMuxer(fileOrParcelFileDescriptor: FileOrParcelFileDescriptor): MediaMuxer =
                openMediaMuxer(fileOrParcelFileDescriptor)
                        ?: throw IllegalStateException(
                                "You didn't initialise your FileOrParcelFileDescriptor correctly!" +
                                        " Only Android Versions over VersionCode.O can manage ParcelFile" +
                                        "Descriptors for MediaMuxers!")
        fun openMediaMuxer(fileOrParcelFileDescriptor: FileOrParcelFileDescriptor): MediaMuxer? =
                if(fileOrParcelFileDescriptor.isParcelFileDescriptor){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        fileOrParcelFileDescriptor.parcelFileDescriptor?.let { openMediaMuxer(it) }
                    else null
                }else MediaMuxer(fileOrParcelFileDescriptor.absolutePath,MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        @RequiresApi(Build.VERSION_CODES.O)
        fun openMediaMuxer(parcelFileDescriptor: ParcelFileDescriptor): MediaMuxer =
                MediaMuxer(parcelFileDescriptor.fileDescriptor,MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    }

    private var parcelFileDescriptor:ParcelFileDescriptor?=null

    private val frameUsec: Long = run {
        (TimeUnit.SECONDS.toMicros(1L) / fps).toLong()
    }
    private var started = false
    private var videoTrackIndex = 0
    private var audioTrackIndex = 0
    private var videoFrames = 0
    private var finalVideoTime: Long = 0

    override fun isStarted(): Boolean {
        return started
    }

    override fun start(videoFormat: MediaFormat, audioExtractor: MediaExtractor?) {
        // now that we have the Magic Goodies, start the muxer
        audioExtractor?.selectTrack(0)
        val audioFormat = audioExtractor?.getTrackFormat(0)
        videoTrackIndex = muxer.addTrack(videoFormat)
        audioFormat?.run {
            audioTrackIndex = muxer.addTrack(audioFormat)
            Timber.tag("Audio format: %s").d(audioFormat.toString())
        }
        Timber.tag("Video format: %s").d(videoFormat.toString())
        muxer.start()
        started = true
    }

    override fun muxVideoFrame(encodedData: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {
        // This code will break if the encoder supports B frames.
        // Ideally we would use set the value in the encoder,
        // don't know how to do that without using OpenGL
        finalVideoTime = frameUsec * videoFrames++
        bufferInfo.presentationTimeUs = finalVideoTime

        muxer.writeSampleData(videoTrackIndex, encodedData, bufferInfo)
    }

    override fun muxAudioFrame(encodedData: ByteBuffer, audioBufferInfo: MediaCodec.BufferInfo) {
        muxer.writeSampleData(audioTrackIndex, encodedData, audioBufferInfo)
    }

    override fun release() {
        muxer.stop()
        parcelFileDescriptor?.close()
        muxer.release()
    }

    override fun getVideoTime(): Long {
        return finalVideoTime
    }

    override fun getMediaMuxer(): MediaMuxer {
        return muxer
    }
}
