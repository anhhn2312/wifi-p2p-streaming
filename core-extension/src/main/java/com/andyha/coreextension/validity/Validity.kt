package com.andyha.coreextension.validity


sealed class Validity<out T> {
    data class Valid<T>(var value: T) : Validity<T>()
    object Invalid : Validity<Nothing>()
    object Initialized : Validity<Nothing>()

    fun validValue(): T? = (this as? Valid)?.value
}