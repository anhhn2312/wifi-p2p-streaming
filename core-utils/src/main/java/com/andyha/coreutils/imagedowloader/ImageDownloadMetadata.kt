package com.andyha.coreutils.imagedowloader

import java.io.File

data class ImageDownloadMetadata(
    val fileName: String = defaultName(),
    val url: String,
    val mimeType: String = "image/png",
    val subPath: String = File.separator + fileName,
    val tokenRequired: Boolean = false
) {
    companion object {

        @JvmStatic
        fun defaultName(): String {
            return "Image-${System.currentTimeMillis()}.png"
        }
    }
}