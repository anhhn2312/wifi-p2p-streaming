package com.andyha.coreextension

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController


fun Fragment.replace(
    containerId: Int,
    fragment: Fragment,
    tag: String? = null,
    addToBackstack: Boolean = false,
    useSwipeToBack: Boolean = false
) {
    replace(
        fragmentManager,
        containerId,
        fragment,
        tag,
        addToBackstack,
        useSwipeToBack
    )
}

fun Fragment.add(
    containerId: Int,
    fragment: Fragment,
    tag: String? = null,
    addToBackstack: Boolean = false
) {
    add(
        fragmentManager,
        containerId,
        fragment,
        tag,
        addToBackstack
    )
}

fun Fragment.getDrawable(@DrawableRes drawableRes: Int): Drawable? =
    ContextCompat.getDrawable(requireContext(), drawableRes)

fun Fragment.isConnected(): Boolean {
    return context?.isConnected() ?: false
}


fun Fragment.getPopupToDefaultAnimation(enter: Boolean, destination: Int): Animation? {
    val navController = findNavController()
    val graph = navController.graph
    val dest = graph.findNode(destination)
    if (!enter && dest != null && navController.currentDestination?.id == dest.id) {
        return AnimationUtils.loadAnimation(requireContext(), R.anim.nav_default_pop_exit_anim)
    }
    return null
}

fun Fragment.getPopupToDefaultAnimation(enter: Boolean, destination: MutableList<Int>): Animation? {
    if (enter) return null
    try {
        val navController = findNavController()
        val graph = navController.graph
        destination.forEach {
            graph.findNode(it)?.let { dest ->
                if (dest.id == navController.currentDestination?.id) {
                    return AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.nav_default_pop_exit_anim
                    )
                }
            }
        }
    } catch (e: Exception) {

    }
    return null
}

fun Fragment.checkPermissionRationale(listPermissions: Array<String>): Boolean {
    var isShow = true
    listPermissions.forEach { permission ->
        if (requireContext().checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
            isShow = shouldShowRequestPermissionRationale(permission)
            if (!isShow) {
                return isShow
            }
        }
    }
    return isShow
}

fun <T> Fragment.getParent(@NonNull fragment: Fragment, @NonNull callbackInterface: Class<T>): T? {
    val parentFragment = fragment.parentFragment
    val fragmentActivity = fragment.activity
    if (callbackInterface.isInstance(parentFragment)) {
        return parentFragment as T
    }
    if (callbackInterface.isInstance(fragmentActivity)) {
        return fragmentActivity as T
    }
    return null
}

fun <T> Fragment.getNavigationResult(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.get<T>(key)

fun <T> Fragment.getNavigationResultLiveData(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun <T> Fragment.setNavigationResult(result: T, key: String = "result") {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}

fun <T : Fragment> FragmentPagerAdapter.getFragmentAtPosition(
    fragmentManager: FragmentManager,
    containerId: Int,
    itemId: Long
): T? {
    val fragmentName = makeFragmentName(containerId, itemId)
    val fragment = fragmentManager.findFragmentByTag(fragmentName)
    return fragment as? T
}

fun <T : Fragment> FragmentPagerAdapter.getFragmentAtPosition(
    fragmentManager: FragmentManager,
    containerId: Int,
    itemId: Int
): T? {
    return getFragmentAtPosition(fragmentManager, containerId, itemId.toLong())
}

private fun makeFragmentName(viewId: Int, id: Long): String {
    return "android:switcher:$viewId:$id"
}