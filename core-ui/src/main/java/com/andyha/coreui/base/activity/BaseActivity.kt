package com.andyha.coreui.base.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.andyha.coredata.storage.preference.languageTag
import com.andyha.coreextension.localehelper.currentLocale
import com.andyha.coreextension.toggleNavigationBarAppearance
import com.andyha.coreextension.updateLanguageResource
import com.andyha.coreui.base.theme.ThemeManager
import com.andyha.coreui.base.theme.ThemeManager.THEME_LIGHT
import com.andyha.coreui.base.theme.theme
import com.andyha.coreui.manager.MotionManager
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import timber.log.Timber


abstract class BaseActivity<VB : ViewBinding>(val bindingFactory: (LayoutInflater) -> VB) :
    HiltActivity() {

    private var _viewBinding: VB? = null

    abstract fun onActivityCreated(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTheme()

        _viewBinding = bindingFactory(layoutInflater)
        setContentView(viewBinding().root)

        if (shouldShowFullscreen()) {
            makeFullScreen()
        }
        if (shouldSupportSharedElementTransition()) {
            setupSharedElementEnter()
            setupSharedElementExitTransition()
        }
        if (savedInstanceState == null) {
            onActivityCreated(savedInstanceState)
        } else {
            //activity recreated
        }
    }

    override fun onResume() {
        super.onResume()
        handleLocaleChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewBinding = null
    }

    private fun makeFullScreen() {
        this.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    fun viewBinding(): VB {
        if (_viewBinding == null) throw NullPointerException("viewBinding is null!!")
        return _viewBinding!!
    }

    open fun onLocaleChanged() {
        Timber.d("onLocaleChange - ${this.javaClass}")
    }

    open fun shouldShowFullscreen(): Boolean {
        return false
    }

    open fun setupTheme() {
        ThemeManager.setTheme(this, preference.theme)
        toggleNavigationBarAppearance(preference.theme == THEME_LIGHT)
    }

    //region Shared element transition
    open fun shouldSupportSharedElementTransition(): Boolean {
        return false
    }

    protected open fun setupSharedElementEnter() {
        val transitionName = intent?.getStringExtra(EXTRA_TRANSITION_NAME) ?: return
        findViewById<View>(android.R.id.content).transitionName = transitionName

        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window?.sharedElementsUseOverlay = false

        window.sharedElementEnterTransition = buildEnterContainerTransform(this)
        window.sharedElementReturnTransition = buildReturnContainerTransform(this)
    }

    protected open fun setupSharedElementExitTransition() {
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window?.sharedElementsUseOverlay = false
        window.sharedElementExitTransition = MotionManager.buildExitHoldTransition(this)
        window?.sharedElementReenterTransition = MotionManager.buildExitHoldTransition(this)
    }

    protected open fun buildEnterContainerTransform(context: Context): MaterialContainerTransform? {
        return MotionManager.buildEnterContainerTransform(context)
    }

    protected open fun buildReturnContainerTransform(context: Context): MaterialContainerTransform? {
        return MotionManager.buildReturnContainerTransform(context)
    }
    //endregion Shared element transition

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateLanguageResource(preference.languageTag)
    }

    private fun handleLocaleChanged() {
        if (currentLocale.toString() != preference.languageTag) {
            localeManager.get().setAppLanguage(this, preference.languageTag)
        }
    }

    open fun hasBackground() = false

    companion object {
        const val EXTRA_TRANSITION_NAME = "EXTRA_TRANSITION_NAME"
    }
}