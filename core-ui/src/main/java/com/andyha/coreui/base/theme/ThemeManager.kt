package com.andyha.coreui.base.theme

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import com.andyha.coredata.storage.preference.AppSharedPreference
import com.andyha.coredata.storage.preference.PreferenceKeys
import com.andyha.coreextension.*
import com.andyha.coreui.R
import com.andyha.coreui.base.activity.BaseActivity
import com.andyha.coreui.base.theme.ThemeManager.THEME_DARK
import com.andyha.coreui.base.theme.ThemeManager.THEME_LIGHT
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


object ThemeManager {

    const val THEME_LIGHT = 1
    const val THEME_DARK = 2

    private val _onThemeChanged = MutableSharedFlow<Int>(replay = 1)
    val onThemeChanged = _onThemeChanged.asSharedFlow()

    fun setTheme(activity: BaseActivity<*>, theme: Int) {
        val style: Int = when (theme) {
            THEME_LIGHT -> R.style.Theme_Light
            else -> R.style.Theme_Dark
        }
        preChangeTheme(activity, theme)
        activity.setTheme(style)
        activity.window.decorView.setBackgroundColor(activity.getColorAttr(android.R.attr.windowBackground))
        _onThemeChanged.tryEmit(theme)
    }

    fun preChangeTheme(activity: BaseActivity<*>, theme: Int) {
        activity.toggleStatusBarAppearance(theme == THEME_LIGHT)
    }

    fun postChangeTheme(activity: BaseActivity<*>, theme: Int) {
        activity.toggleNavigationBarAppearance(theme == THEME_LIGHT)
    }

    fun getTextColor(context: Context, attr: Int): Int? {
        return TextColorAttr.getTextColorAttr(attr)?.let { context.getColorAttr(it) }
    }

    fun getIconTint(context: Context, attr: Int): ColorStateList? {
        return IconTintAttr.getIconTintAttr(attr)
            ?.let { context.getColorStateListAttr(R.attr.colorIconTintPrimary) }
    }

    fun getBackgroundDrawawble(context: Context, attr: Int): Drawable? {
        return BackgroundDrawableAttr.getBackgroundDrawableAttr(attr)
            ?.let { context.getDrawableAttr(it) }
    }

    fun getBackgroundColor(context: Context, attr: Int): Int? {
        return BackgroundColorAttr.getBackgroundColor(attr)?.let { context.getColorAttr(it) }
    }
}

inline var AppSharedPreference.theme: Int
    get() = this.getInt(PreferenceKeys.APP_THEME, THEME_LIGHT)
    set(value) = this.set(PreferenceKeys.APP_THEME, value)

fun AppSharedPreference.toggleTheme(): Int {
    theme = if (theme == THEME_DARK) THEME_LIGHT
    else THEME_DARK
    return theme
}