package com.andyha.coretesting.livedata

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry



class LiveDataTestLifecycle private constructor() : LifecycleOwner {
    private val registry = LifecycleRegistry(this)
    fun create(): LiveDataTestLifecycle {
        return handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    fun start(): LiveDataTestLifecycle {
        return handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    fun resume(): LiveDataTestLifecycle {
        return handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    fun pause(): LiveDataTestLifecycle {
        return handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    fun stop(): LiveDataTestLifecycle {
        return handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    fun destroy(): LiveDataTestLifecycle {
        return handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    val currentState: Lifecycle.State
        get() = registry.currentState

    private fun handleLifecycleEvent(event: Lifecycle.Event): LiveDataTestLifecycle {
        registry.handleLifecycleEvent(event)
        return this
    }

    override fun getLifecycle(): Lifecycle {
        return registry
    }

    companion object {
        fun initialized(): LiveDataTestLifecycle {
            return LiveDataTestLifecycle()
        }

        fun resumed(): LiveDataTestLifecycle {
            return initialized().resume()
        }
    }
}