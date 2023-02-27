package com.andyha.p2p.streaming.kit.encoder

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import java.nio.ByteBuffer

interface FrameMuxer {

    fun isStarted(): Boolean

    fun start(videoFormat: MediaFormat, audioExtractor: MediaExtractor? = null)

    fun muxVideoFrame(byteBuffer: ByteBuffer, bufferInfo: MediaCodec.BufferInfo)

    fun muxAudioFrame(encodedData: ByteBuffer, audioBufferInfo: MediaCodec.BufferInfo)

    fun release()

    fun getVideoTime(): Long

    fun getMediaMuxer(): MediaMuxer
}
