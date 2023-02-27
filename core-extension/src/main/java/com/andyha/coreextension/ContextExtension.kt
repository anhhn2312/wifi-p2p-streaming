package com.andyha.coreextension

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.ContentUris
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.andyha.coreextension.localehelper.Locales
import com.andyha.coreextension.localehelper.currentLocale
import com.andyha.coreextension.utils.ValidationRegex
import timber.log.Timber
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


/**
 * Check if the device is connected to network or not
 */
fun Context.isConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    val cellularNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
    return wifiNetwork?.isConnected == true || cellularNetwork?.isConnected == true
}

fun Context.formatString(source: Int, arg: Int): String {
    return String.format(this.getString(source), getString(arg))
}

fun Context.formatString(source: Int, arg: String): String {
    return String.format(this.getString(source), arg)
}

fun isAndroidQOrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

fun isAndroidROrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

fun Context.launchApp(packageName: String) {
    startActivity(packageManager.getLaunchIntentForPackage(packageName))
}

fun Context.showInMarket(packageName: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
}

fun Context.isAppRunning(): Boolean {
    val activityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val procInfos = activityManager.runningAppProcesses
    if (procInfos != null) {
        for (processInfo in procInfos) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                processInfo.processName == this.packageName
            ) {
                return true
            }
        }
    }
    return false
}


fun formatCountdownTime(time: Long): String {
    return String.format(
        "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(time),
        TimeUnit.MILLISECONDS.toSeconds(time) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
    )
}

fun Context.getStringByName(aString: String): String? {
    return try {
        val resId: Int = resources.getIdentifier(aString, "string", packageName)
        getString(resId)
    } catch (e: Exception) {
        Timber.e(e)
        null
    }
}

fun Context.getStringResourceByName(aString: String): Int? {
    return try {
        resources.getIdentifier(aString, "string", packageName)
    } catch (e: Exception) {
        Timber.e(e)
        null
    }
}

fun Context.getDrawableResourceByName(aDrawable: String): Int? {
    return try {
        resources.getIdentifier(aDrawable, "drawable", packageName)
    } catch (e: Exception) {
        Timber.e(e)
        null
    }
}

fun Context.getColorResourceByName(aColor: String): Int? {
    return try {
        resources.getIdentifier(aColor, "color", packageName)
    } catch (e: Exception) {
        Timber.e(e)
        null
    }
}

fun Context.getColorAttr(attr: Int): Int {
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attr))
    val color = typedArray.getColor(0, Color.parseColor("#26999999"))
    typedArray.recycle()
    return color
}

fun Context.getColorStateListAttr(attr: Int): ColorStateList? {
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attr))
    val colorStateList = typedArray.getColorStateList(0)
    typedArray.recycle()
    return colorStateList
}

fun Context.getDimenAttr(attr: Int): Int {
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attr))
    val dimen = typedArray.getDimensionPixelSize(0, 0)
    typedArray.recycle()
    return dimen
}

fun Context.getDrawableAttr(attr: Int): Drawable? {
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attr))
    val drawable = typedArray.getDrawable(0)
    typedArray.recycle()
    return drawable
}

fun Context.isPackageExisted(targetPackage: String): Boolean {
    return packageManager.getInstalledApplications(0).any { it.packageName == targetPackage }
}

fun Context.permissionsIsGranted(listPermissions: Array<String>): Boolean {
    listPermissions.forEach { permission ->
        if (ContextCompat.checkSelfPermission(this, permission) != PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

fun Context.getAddressFromLocation(latitude: Double, longitude: Double): String? {
    try {
        val currentLanguage = this.currentLocale.language
        var locale = Locale.US
        if (currentLanguage == "vi") {
            locale = Locale("vi", "VN")
        }
        val addresses: List<Address>
        val geoCoder = Geocoder(this, locale)
        addresses = geoCoder.getFromLocation(latitude, longitude, 1) ?: listOf()
        Timber.d("GetAddress: ${addresses[0]}")
        var ret = ""
        return addresses[0].let {
            if (!it.subAdminArea.isNullOrEmpty()) {
                ret += it.subAdminArea
            } else if (!it.locality.isNullOrEmpty()) {
                ret += it.locality
            }
            if (!it.adminArea.isNullOrEmpty()) {
                if (ret.isNotEmpty()) ret += ", "
                ret += it.adminArea
            }
            ret
        }
    } catch (er: Exception) {
        return null
    }
}

fun Context.getTypeFace(@FontRes resId: Int): Typeface? {
    return ResourcesCompat.getFont(this, resId)
}

fun Context.dial(phoneNumber: String?) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$phoneNumber")
    this.startActivity(intent)
}

/**
 * Open a url in a custom tab
 *
 * @param url
 */
fun Context.openUrl(url: String) {
    val tabIntent = CustomTabsIntent.Builder()
        .setToolbarColor(getColorAttr(R.attr.colorPrimary))
        .enableUrlBarHiding()
        .build()

    kotlin.runCatching {
        tabIntent.launchUrl(this, Uri.parse(url).normalizeScheme())
    }
}

fun Context.openFile(path: String) {
    val file = File(path)

    // Get URI and MIME type of file
    val uri: Uri
    val mime: String?

    try {
        uri = FileProvider.getUriForFile(this, this.packageName + ".fileprovider", file)
        mime = this.contentResolver.getType(uri)
    } catch (e: Exception) {
        Timber.e(e)
        return
    }

    // Open file with user selected app
    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        setDataAndType(uri, mime)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}

fun Context.sendEmail(email: String?) {
    this.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")))
}

fun Context.getColorCompat(id: Int): Int = ContextCompat.getColor(this, id)

fun Context.getDrawableCompat(id: Int): Drawable? = ContextCompat.getDrawable(this, id)

fun Context.formatStringResource(resId: Int, param: String?): String? =
    String.format(this.getString(resId), param)

fun Context.getAttributeDrawable(resource: Int): Drawable? {
    val outValue = TypedValue()
    theme.resolveAttribute(resource, outValue, true)
    return AppCompatResources.getDrawable(this, outValue.resourceId)
}

fun Context.dimen(@DimenRes dimen: Int) = resources.getDimensionPixelSize(dimen)

fun Context.font(@FontRes font: Int): Typeface? {
    return ResourcesCompat.getFont(this, font)
}

fun isPhoneNumberValid(phoneNumber: String): Boolean {
    return Pattern.matches(ValidationRegex.REGEX_TELEPHONE_NUMBER, phoneNumber)
}

fun Context.getActivity(): Activity? {
    if (this is Activity) {
        return this
    } else {
        val contextWrapper = this as? ContextWrapper
        val baseContext = contextWrapper?.baseContext
            ?: return null
        return baseContext.getActivity()
    }
}


fun Context.getCountryViaNetwork(): String? {
    try {
        val tm = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountry = tm.simCountryIso
        if (simCountry != null && simCountry.length == 2) { // SIM country code is available
            return simCountry
        } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
            val networkCountry = tm.networkCountryIso
            if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                return networkCountry
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun Context?.getUserCountry(): String? {
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Resources.getSystem().configuration.locales[0]
    } else {
        Resources.getSystem().configuration.locale
    }

    var country: String?

    country = this?.getCountryViaNetwork()?.toUpperCase(Locale.ROOT)

    if (country == null) {
        country = locale.country
    }
    return country
}

fun Context.layoutInflater(): LayoutInflater = LayoutInflater.from(this)

fun Context.isCurrentLanguageVietnamese(): Boolean =
    currentLocale.language == Locales.Vietnamese.language

fun Context.openAppSettings() {
    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:$packageName")
    })
}

@SuppressLint("HardwareIds")
fun Context.getDeviceId(): String? {
    return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
}

@SuppressLint("HardwareIds")
fun Context.getDeviceName(): String? {
    return Settings.Secure.getString(contentResolver, "bluetooth_name")
}

fun Context.enableGPS(status: Boolean) {
    Settings.Secure.setLocationProviderEnabled(
        applicationContext.contentResolver,
        LocationManager.GPS_PROVIDER,
        status
    )
}


fun Context.getGPSStatus(): Boolean {
    return Settings.Secure.isLocationProviderEnabled(
        applicationContext.contentResolver,
        LocationManager.GPS_PROVIDER
    )
}

fun Context.clearNotification(id: Int) {
    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(id)
}


fun Context.inflateNoParent(resId: Int): View {
    return LayoutInflater.from(this).inflate(resId, null, false)
}

fun Context.getScreenOrientation(): Int {
    return resources.configuration.orientation
}