@file:JvmName("LiveDataTestObserverKt")

package com.andyha.coretesting.livedata

import androidx.lifecycle.LiveData

fun <T> LiveData<T>.test(): LiveDataTestObserver<T> {
  return LiveDataTestObserver.test(this)
}
