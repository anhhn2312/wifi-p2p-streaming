package com.andyha.coreextension

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import timber.log.Timber

suspend fun <T> Flow<T>.bindTo(flow: MutableStateFlow<T>) {
    collect { flow.emit(it) }
}

suspend fun <T> Flow<T>.bindTo(flow: MutableSharedFlow<T>) {
    collect { flow.emit(it) }
}

fun <T> Flow<T>.filterNotNull(): Flow<T> =
    filter { it != null }.map { it }

fun <T> SharedFlow<T>.getValueOrNull(): T? {
    return runBlocking(Dispatchers.Default) {
        when (replayCache.isEmpty()) {
            true -> null
            else -> firstOrNull()
        }
    }
}

suspend fun <T> Flow<T>.collectSafely(action: ((T) -> Unit)? = null) =
    catch { Timber.e(it) }.collect { action?.invoke(it) }

suspend fun <T> Flow<T>.collectLatestSafely(action: ((T) -> Unit)? = null) =
    catch { Timber.e(it) }.collectLatest { action?.invoke(it) }