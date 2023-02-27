package com.andyha.p2p.streaming.kit.encoder


interface MuxingResult

data class MuxingSuccess(
    val file: FileOrParcelFileDescriptor
): MuxingResult

data class MuxingError(
    val message: String,
    val exception: Exception
): MuxingResult

class MuxingPending: MuxingResult