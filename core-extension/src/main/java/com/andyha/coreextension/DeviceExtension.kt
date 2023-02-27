package com.andyha.coreextension

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity


fun Context.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = this.resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun Context.getDeviceDensityString(): String {
    val densityDpi = this.resources.displayMetrics.densityDpi
    var textDensity = ""
    when (densityDpi) {
        DisplayMetrics.DENSITY_LOW -> {
            textDensity = "ldpi"
        }

        DisplayMetrics.DENSITY_MEDIUM -> {
            textDensity = "mdpi"
        }

        DisplayMetrics.DENSITY_TV,
        DisplayMetrics.DENSITY_HIGH -> {
            textDensity = "hdpi"
        }

        DisplayMetrics.DENSITY_260,
        DisplayMetrics.DENSITY_280,
        DisplayMetrics.DENSITY_300,
        DisplayMetrics.DENSITY_XHIGH -> {
            textDensity = "xhdpi"
        }

        DisplayMetrics.DENSITY_340,
        DisplayMetrics.DENSITY_360,
        DisplayMetrics.DENSITY_400,
        DisplayMetrics.DENSITY_420,
        DisplayMetrics.DENSITY_440,
        DisplayMetrics.DENSITY_XXHIGH -> {
            textDensity = "xxhdpi"
        }

        DisplayMetrics.DENSITY_560,
        DisplayMetrics.DENSITY_XXXHIGH -> {
            textDensity = "xxxhdpi"
        }
        else -> textDensity = "---OTHER---"
    }

    return textDensity
}

fun FragmentActivity.clearDarkStatusBarIcons() {
    this.window?.decorView?.let { decoder ->
        decoder.systemUiVisibility =
            decoder.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun FragmentActivity.setStatusBarColor(colorAttr: Int = androidx.appcompat.R.attr.colorAccent) {
    window?.statusBarColor = getColorAttr(colorAttr)
}

@SuppressLint("HardwareIds")
fun Context.imei(slot: Int = 0): String {
    val mTelephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    var result = ""
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result = (mTelephonyManager.getImei(slot))
        } else {
            mTelephonyManager.getDeviceId(slot)
        }
        if (result.isEmpty()) result = mTelephonyManager.getDeviceId(slot)
    }
    return result
}


@SuppressLint("HardwareIds")
fun Context.serialNumber(): String {
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Build.getSerial()
        } else {
            Build.SERIAL
        }
    }
    return ""
}

fun Context.targetSdkVersion(): Int{
    return packageManager.getPackageInfo(packageName, 0).applicationInfo.targetSdkVersion
}

fun Context.versionCode(): Int{
    return try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        val version = pInfo.versionCode
        version
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        0
    }
}

fun Context.getAppName(): String = applicationInfo.loadLabel(packageManager).toString()

fun Context.versionName(): String {
    return try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        val version = pInfo.versionName
        version
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        ""
    }
}

fun Context.dpToPx(dpValue: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, resources.displayMetrics)
        .toInt()
}