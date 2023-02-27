package com.andyha.coreextension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer


open class SingleLiveData<T> : MutableLiveData<T?>() {

    override fun observe(owner: LifecycleOwner, observer: Observer<in T?>) {
        super.observe(owner) { t ->
            if (t != null) {
                observer.onChanged(t)
                postValue(null)
            }
        }
    }
}