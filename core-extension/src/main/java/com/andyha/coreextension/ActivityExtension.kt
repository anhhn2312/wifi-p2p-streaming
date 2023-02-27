package com.andyha.coreextension

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.*


fun AppCompatActivity.replace(
    containerId: Int,
    fragment: Fragment,
    tag: String? = null,
    addToBackstack: Boolean = false,
    callback: ((Fragment) -> Unit)? = null,
    useSwipeToBack: Boolean = false
) {
    replace(
        supportFragmentManager,
        containerId,
        fragment,
        tag,
        addToBackstack,
        useSwipeToBack
    )
}

fun replace(
    fragmentManager: FragmentManager?,
    containerId: Int,
    fragment: Fragment,
    tag: String? = null,
    addToBackstack: Boolean = false,
    useSwipeToBack: Boolean = false
) {
    val fragmentTransaction =
        fragmentManager?.beginTransaction()

    fragmentTransaction?.setCustomAnimations(
        R.animator.fade_in,
        R.animator.fade_out,
        R.animator.fade_in,
        R.animator.fade_out
    )

    if (useSwipeToBack) {
        fragmentTransaction?.add(containerId, fragment, tag ?: fragment.getSimpleName())
    } else {
        fragmentTransaction?.replace(containerId, fragment, tag ?: fragment.getSimpleName())
    }
    fragmentTransaction?.let {
        if (addToBackstack) {
            it.addToBackStack(tag ?: fragment.getSimpleName())
        }
    }
    fragmentTransaction?.commit()
}

fun AppCompatActivity.add(
    containerId: Int,
    fragment: Fragment,
    tag: String? = null,
    addToBackstack: Boolean = false,
    aniBotToTop: Boolean = false,
    callback: ((Fragment) -> Unit)? = null
) {
    add(
        supportFragmentManager,
        containerId,
        fragment,
        tag,
        addToBackstack,
        aniBotToTop
    )
}

fun add(
    fragmentManager: FragmentManager?,
    containerId: Int,
    fragment: Fragment,
    tag: String? = null,
    addToBackstack: Boolean = false,
    aniBotToTop: Boolean = false
) {
    val fragmentTransaction =
        fragmentManager?.beginTransaction()

    if (aniBotToTop) {
        fragmentTransaction?.setCustomAnimations(
            R.animator.bottom_up,
            R.animator.nothing,
            R.animator.nothing,
            R.animator.bottom_down
        )
    } else {
        fragmentTransaction?.setCustomAnimations(
            R.animator.fade_in,
            R.animator.fade_out,
            R.animator.fade_in,
            R.animator.fade_out
        )
    }

    fragmentTransaction?.add(containerId, fragment, tag ?: fragment.getSimpleName())
    fragmentTransaction?.let {
        if (addToBackstack) {
            it.addToBackStack(tag ?: fragment.getSimpleName())
        }
    }
    fragmentTransaction?.commit()
}

fun getTopFragment(manager: FragmentManager?): Fragment? {
    if (manager != null) {
        if (manager.backStackEntryCount > 0) {
            val backStackEntry = manager.getBackStackEntryAt(manager.backStackEntryCount - 1)
            return manager.findFragmentByTag(backStackEntry.name)
        } else {
            val fragments = manager.fragments
            if (fragments.size > 0) {
                if (fragments[0] is NavHostFragment) {
                    if (!fragments[0].isAdded) return null
                    val listFragment = fragments[0].childFragmentManager.fragments
                    if (listFragment.size > 0) {
                        return listFragment[0]
                    }
                }
                return fragments[0]
            }
        }
    }
    return null
}

fun Activity.showKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}

fun hideSoftKeyboard(activity: Activity?) {
    val inputMethodManager =
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    // Check if no view has focus
    val currentFocusedView = activity.currentFocus
    currentFocusedView?.let {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

@SuppressLint("ClickableViewAccessibility")
fun Activity.setupUI(view: View) {
    // Set up touch listener for non-text box views to hide keyboard.
    if (view !is EditText) {
        view.setOnTouchListener { _, _ ->
            this.hideKeyboard()
            return@setOnTouchListener false
        }
    }
    //If a layout container, iterate over children and seed recursion.
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val innerView = view.getChildAt(i)
            setupUI(innerView)
        }
    }
}


fun Snackbar.materialize(drawable: Int? = null): Snackbar {
    val params = this.view.layoutParams as ViewGroup.MarginLayoutParams
    val margin = context.dimen(R.dimen.dimen16dp)
    params.setMargins(margin, margin, margin, 12)
    this.view.layoutParams = params
    this.view.background = context.getDrawableCompat(drawable ?: R.drawable.snackbar_background)
    ViewCompat.setElevation(this.view, 6f)
    return this
}

fun Int.toPx(): Int {
    val density = Resources.getSystem().displayMetrics.density
    return (this * density + .5F).toInt()
}

fun hide(
    fragmentManager: FragmentManager?,
    fragment: Fragment,
    animTopToBottom: Boolean = false
) {
    val fragmentTransaction =
        fragmentManager?.beginTransaction()

    if (animTopToBottom) {
        fragmentTransaction?.setCustomAnimations(
            R.animator.nothing,
            R.animator.bottom_down,
            R.animator.nothing,
            R.animator.nothing
        )
    } else {
        fragmentTransaction?.setCustomAnimations(
            R.animator.fade_in,
            R.animator.fade_out,
            R.animator.fade_in,
            R.animator.fade_out
        )
    }

    fragmentTransaction?.hide(fragment)
    fragmentTransaction?.commitAllowingStateLoss()
}

fun show(
    fragmentManager: FragmentManager?,
    fragment: Fragment,
    aniBotToTop: Boolean = false
) {
    val fragmentTransaction =
        fragmentManager?.beginTransaction()

    if (aniBotToTop) {
        fragmentTransaction?.setCustomAnimations(
            R.animator.bottom_up,
            R.animator.nothing,
            R.animator.nothing,
            R.animator.nothing
        )
    } else {
        fragmentTransaction?.setCustomAnimations(
            R.animator.fade_in,
            R.animator.fade_out,
            R.animator.fade_in,
            R.animator.fade_out
        )
    }

    fragmentTransaction?.show(fragment)
    fragmentTransaction?.commitAllowingStateLoss()
}

fun Activity.updateLanguageResource(langTag: String): Configuration? {
    val configuration = resources.configuration
    try {
        val locale = langTag.convertToLocale()
        Timber.d("langTag = $langTag - current: $locale")
        Locale.setDefault(locale)
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        applicationContext.resources.updateConfiguration(configuration, resources.displayMetrics)
    } catch (e: Exception) {
        Timber.e(e)
    }
    return configuration
}

fun Activity?.openFingerprintScreen() {
//    try {
//        val action = when {
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> Settings.ACTION_FINGERPRINT_ENROLL
//            else -> Settings.ACTION_SECURITY_SETTINGS
//        }
//        this?.startActivity(Intent(action))
//    } catch (e: Exception) {
//        Timber.e("openFingerprintScreen error: ${e.message}")
    this?.startActivity(Intent(Settings.ACTION_SETTINGS))
//    }
}

fun Activity.getNavGraph(navController: NavController?, navigation: Int): NavGraph? {
    return try {
        navController?.graph
    } catch (e: IllegalStateException) {
        val inflater = navController?.navInflater
        return inflater?.inflate(navigation)
    }
}

fun Activity.toggleStatusBarAppearance(isLight: Boolean) {
    val decorView = window.decorView
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.setSystemBarsAppearance(
            if (isLight) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else{
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        var flags = decorView.systemUiVisibility
        if (isLight) {
            if (flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR == 0) {
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                decorView.systemUiVisibility = flags
            }
        } else {
            if (flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR != 0) {
                flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                decorView.systemUiVisibility = flags
            }
        }
    }
}

fun Activity.toggleNavigationBarAppearance(isLight: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.setSystemBarsAppearance(
            if (isLight) WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS else 0,
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val decorView = window.decorView
        var flags = decorView.systemUiVisibility
        flags = if (isLight) {
            flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
        decorView.systemUiVisibility = flags
    }
    window.navigationBarColor = getColorAttr(R.attr.windowBackgroundVariant)
}