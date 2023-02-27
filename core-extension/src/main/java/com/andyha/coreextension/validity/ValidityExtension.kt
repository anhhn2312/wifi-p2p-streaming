package com.andyha.coreextension.validity

import com.andyha.coreextension.validity.Validity.*
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.*


fun <T> Observable<Validity<T>>.doOnInvalid(onInvalid: () -> Unit): Observable<Validity<T>> =
    map {
        if (it is Invalid) {
            onInvalid.invoke()
        }
        return@map it
    }

fun <T> Observable<Validity<T>>.doOnInitialized(onInitialized: () -> Unit): Observable<Validity<T>> =
    map {
        if (it is Initialized) {
            onInitialized.invoke()
        }
        return@map it
    }

fun <T> Observable<Validity<T>>.filterValid(): Observable<T> =
    filter { it is Valid }.map { (it as Valid).value }


fun <T> Flow<Validity<T>>.doOnInvalid(onInvalid: () -> Unit): Flow<Validity<T>> =
    map {
        if (it is Invalid) {
            onInvalid.invoke()
        }
        return@map it
    }

fun <T> Flow<Validity<T>>.doOnInitialized(onInitialized: () -> Unit): Flow<Validity<T>> =
    map {
        if (it is Initialized) {
            onInitialized.invoke()
        }
        return@map it
    }

fun <T> Flow<Validity<T>>.filterValid(): Flow<T> =
    filter { it is Valid }.map { (it as Valid).value }

fun <T> StateFlow<Validity<T>>.value(): T? {
    if (replayCache.isEmpty()) return null
    return replayCache.let { it[it.size - 1] }.validValue()
}

fun <T> SharedFlow<Validity<T>>.value(): T? {
    if (replayCache.isEmpty()) return null
    return replayCache.let { it[it.size - 1] }.validValue()
}


