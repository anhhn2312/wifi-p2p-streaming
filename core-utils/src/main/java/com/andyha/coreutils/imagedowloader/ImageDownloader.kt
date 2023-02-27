package com.andyha.coreutils.imagedowloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.annotation.WorkerThread
import com.andyha.coredata.storage.preference.AppSharedPreference
import com.andyha.coredata.storage.preference.token
import com.andyha.corenetwork.config.NetworkConfigConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request


/**
 * Download images, store in internal storage and share images. Sample:
 *
 * ImageSharer.Builder(requireContext())
 *  .text("airplane images")
 *  .image("https://homepages.cae.wisc.edu/~ece533/images/airplane.png")
 *  .progressListener { progress ->
 *      when (progress) {
 *          is ImageSharer.Progress.Downloaded -> TODO()
 *          is ImageSharer.Progress.Error -> TODO()
 *          is ImageSharer.Progress.Saved -> TODO()
 *          is ImageSharer.Progress.Shared -> TODO()
 *          is ImageSharer.Progress.Started -> TODO()
 *      }
 *  }
 *  .build()
 *  .launch()
 *  .disposedBy(compositeDisposable)
 */

class ImageDownloader private constructor(
    context: Context,
    private val images: List<ImageDownloadMetadata>,
    private val storedDirectory: String,
    private val networkType: Int,
    private val progressListener: ((Progress) -> Unit)?
) {

    private val downloadManager: DownloadManager =
        context.applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val token: String
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    init {
        val preference = AppSharedPreference.getInstance()
        token = "${NetworkConfigConstants.BEARER} ${preference.token}"
    }

    private fun enqueueRequest(
        dm: DownloadManager?,
        metadata: ImageDownloadMetadata,
        token: String,
        storedDirectory: String,
        networkType: Int
    ) {
        val downloadUri = Uri.parse(getActualUrl(metadata, token))
        val request: DownloadManager.Request = DownloadManager.Request(downloadUri)
            .apply {
                setAllowedNetworkTypes(networkType)
                    .setAllowedOverRoaming(false)
                    .setTitle(metadata.fileName)
                    .setMimeType(metadata.mimeType)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(storedDirectory, metadata.subPath)
            }
        dm?.enqueue(request)
    }

    @WorkerThread
    private fun getActualUrl(metadata: ImageDownloadMetadata, token: String): String {
        if (!metadata.tokenRequired) {
            return metadata.url
        }

        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(metadata.url)
            .addHeader(NetworkConfigConstants.AUTHORIZATION, token)
            .build()

        return client.newCall(request).execute().use { response ->
            response.request.url.toString()
        }
    }

    fun launch() {
        coroutineScope.launch {
            flow {
                images.forEachIndexed { index, metadata ->
                    enqueueRequest(downloadManager, metadata, token, storedDirectory, networkType)
                    emit(Progress.Enqueued(index))
                }
            }
                .flowOn(Dispatchers.IO)
                .onStart { progressListener?.invoke(Progress.Started) }
                .onCompletion { progressListener?.invoke(Progress.Complete) }
                .catch { progressListener?.invoke(Progress.Error(it)) }
                .collect { progressListener?.invoke(it) }
        }
    }

    class Builder(private val context: Context) {

        private var imageMetadatas: ArrayList<ImageDownloadMetadata> = arrayListOf()
        private var storedDirectory: String = Environment.DIRECTORY_DOWNLOADS
        private var networkType: Int =
            DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
        private var progressListener: ((Progress) -> Unit)? = null

        fun image(metadata: ImageDownloadMetadata): Builder {
            this.imageMetadatas.add(metadata)
            return this
        }

        fun images(images: List<ImageDownloadMetadata>): Builder {
            this.imageMetadatas.addAll(images)
            return this
        }

        fun storedDirectory(directory: String): Builder {
            this.storedDirectory = directory
            return this
        }

        fun networkType(networkType: Int): Builder {
            this.networkType = networkType
            return this
        }

        fun progressListener(progressListener: ((Progress) -> Unit)?): Builder {
            this.progressListener = progressListener
            return this
        }

        fun build(): ImageDownloader {
            return ImageDownloader(
                context,
                imageMetadatas,
                storedDirectory,
                networkType,
                progressListener
            )
        }
    }

    sealed class Progress {
        object Started : Progress()
        class Enqueued(val index: Int) : Progress()
        object Complete : Progress()
        class Error(val error: Throwable) : Progress()
    }
}