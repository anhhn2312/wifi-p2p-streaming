package com.andyha.coreutils.permission

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.andyha.coreextension.subscribeSafely
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.functions


class RxPermission private constructor(private val context: Context) {
    private var behaviorSubject = BehaviorSubject.create<Boolean>()
    private var disposable: Disposable? = null
    private lateinit var permissions: Array<String>
    private var onDenyForever: (() -> Unit)? = null
    private var reasonTitle: String? = null
    private var reasonDesc: String? = null

    fun withPermission(permission: String): RxPermission {
        this.permissions = arrayOf(permission)
        this.reasonTitle = null
        this.reasonDesc = null
        this.onDenyForever = null
        return this
    }

    fun withPermissions(permissions: Array<String>): RxPermission {
        this.permissions = permissions
        return this
    }


    fun withReason(title: String?, desc: String?): RxPermission {
        this.reasonTitle = title
        this.reasonDesc = desc
        return this
    }

    fun doOnDenyForever(callback: (() -> Unit)? = null): RxPermission {
        this.onDenyForever = callback
        return this
    }

    /**
     * Callback invoked only when permissions are granted
     */
    fun request(onSuccess: () -> Unit) {

        if (disposable != null) {
            disposable!!.dispose()
            behaviorSubject = BehaviorSubject.create()
        }

        startPermissionsAsk()

        disposable = behaviorSubject
            .onErrorReturn { false }
            .subscribeSafely { if (it) onSuccess.invoke() }
    }

    /**
     * Callback invoked true/false when permission are granted or not
     * @param result
     */
    fun requestForFullResult(result: (Boolean) -> Unit) {

        if (disposable != null) {
            disposable!!.dispose()
            behaviorSubject = BehaviorSubject.create()
        }

        startPermissionsAsk()

        disposable = behaviorSubject
            .onErrorReturn { false }
            .subscribeSafely { result.invoke(it) }
    }

    private fun startPermissionsAsk() {
        val shadowActivity =
            Class.forName("com.andyha.coreui.base.permission.ShadowActivity").kotlin
        val companionObject = shadowActivity.companionObject
        val companionInstance = shadowActivity.companionObjectInstance
        val function = companionObject?.functions?.first { it.name == "start" }
        function?.call(
            companionInstance,
            context,
            permissions,
            reasonTitle,
            reasonDesc,
            onDenyForever
        )
    }

    fun onPermissionResult(result: Boolean) {
        behaviorSubject.onNext(result)
    }

    fun isPermissionGranted(permission: String): Boolean {
        return isPermissionGranted(arrayOf(permission))
    }

    private fun isPermissionGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context.applicationContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) return false
        }
        return true
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: RxPermission? = null

        @JvmStatic
        fun getInstance(context: Context): RxPermission {
            if (instance == null) {
                instance = RxPermission(context)
            }
            return instance!!
        }

        fun init(context: Context) {
            getInstance(context)
        }

        fun clear() {
            instance = null
        }
    }
}