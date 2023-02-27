package com.andyha.coreextension

import android.content.Intent


fun Intent.getIntOrNull(key: String): Int?{
    if(!hasExtra(key)) return null

    return getIntExtra(key, 0)
}

fun Intent.getStringOrNull(key: String): String?{
    if(!hasExtra(key)) return null

    return getStringExtra(key)
}

fun Intent.getDoubleOrNull(key: String): Double?{
    if(!hasExtra(key)) return null

    return getDoubleExtra(key, 0.0)
}

fun Intent.getFloatOrNull(key: String): Float?{
    if(!hasExtra(key)) return null

    return getFloatExtra(key, 0f)
}

fun Intent.getLongOrNull(key: String): Long?{
    if(!hasExtra(key)) return null

    return getLongExtra(key, 0)
}

fun Intent.getBooleanOrNull(key: String): Boolean?{
    if(!hasExtra(key)) return null

    return getBooleanExtra(key, false)
}