package com.andyha.coreextension

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit


fun <T> Observable<T>.uiThread() = this.subscribeOn(ExecutorManager.io())
    .observeOn(AndroidSchedulers.mainThread())

fun Completable.uiThread() = this.subscribeOn(ExecutorManager.io())
    .observeOn(AndroidSchedulers.mainThread())!!

fun <T> Single<T>.uiThread() = this.subscribeOn(ExecutorManager.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.workerThread() = this.subscribeOn(ExecutorManager.io())

fun Completable.workerThread() = this.subscribeOn(ExecutorManager.io())

fun <T> Single<T>.workerThread() = this.subscribeOn(ExecutorManager.io())

fun <T> PublishSubject<T>.debounce(work: (() -> Unit)? = null, timeoutMs: Long = 500) {
    debounce(timeoutMs, TimeUnit.MILLISECONDS)
        .observeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : Observer<T> {
            override fun onSubscribe(d: Disposable) {}
            override fun onError(e: Throwable) {}
            override fun onComplete() {}

            override fun onNext(t: T) {
                //do work
                work?.invoke()
            }
        })
}

fun Disposable.disposedBy(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

fun <T> ObservableEmitter<T>.checkDisposed(): ObservableEmitter<T>? {
    return if (this.isDisposed) {
        null
    } else {
        this
    }
}

fun CompletableEmitter.checkDisposed(): CompletableEmitter? {
    return if (this.isDisposed) {
        null
    } else {
        this
    }
}

fun <T> Observable<T>.filterNotNull(): Observable<T> =
    filter { it != null }.map { it }

fun Observable<String>.filterEmpty(): Observable<String> =
    filter { it.isNotEmpty() }.map { it }

@JvmName("bindNullable")
fun <T> Observable<T>.bindTo(liveData: MutableLiveData<T?>): Disposable =
    subscribe({
        liveData.postValue(it)
    }, {
        Timber.e(it)
    })

fun <T> Observable<T>.bindTo(liveData: MutableLiveData<T>): Disposable =
    subscribe({
        liveData.postValue(it)
    }, {
        Timber.e(it)
    })

fun <T> Single<T>.bindTo(liveData: MutableLiveData<T?>): Disposable =
    subscribe({
        liveData.postValue(it)
    }, {
        Timber.e(it)
    })

fun <T> Observable<T>.bindTo(observableField: ObservableField<T?>): Disposable =
    subscribe({
        observableField.set(it)
    }, {
        Timber.e(it)
    })

fun <T> Observable<T>.subscribeSafely(onNext: ((T) -> Unit)? = null): Disposable =
    subscribe(onNext ?: {}, { Timber.e(it) })

fun <T> Single<T>.subscribeSafely(onSuccess: ((T) -> Unit)? = null): Disposable =
    subscribe(onSuccess ?: {}, { Timber.e(it) })

fun Completable.subscribeSafely(onComplete: (() -> Unit)? = null): Disposable =
    subscribe(onComplete ?: {}, { Timber.e(it) })