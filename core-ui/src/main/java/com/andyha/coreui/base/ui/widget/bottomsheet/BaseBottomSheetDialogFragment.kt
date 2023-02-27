package com.andyha.coreui.base.ui.widget.bottomsheet

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.andyha.coreextension.expandClickArea
import com.andyha.coreextension.setOnGlobalLayoutListener
import com.andyha.coreextension.utils.DimensionUtils
import com.andyha.coreui.R
import com.andyha.coreui.base.ui.widget.progress.ProgressDialog
import com.andyha.coreui.base.ui.widget.showToast
import com.andyha.coreui.base.ui.widget.textview.BaseTextView
import com.andyha.coreui.base.ui.widget.transitionbutton.TransitionButton
import com.andyha.coreui.base.viewModel.BaseViewModel
import com.andyha.coreui.base.viewModel.ProgressType
import com.andyha.coreui.base.viewModel.ViewModelState
import com.andyha.coreui.databinding.BaseBottomSheetDialogBinding
import com.andyha.coreui.manager.ApiErrorHandler.Companion.ERROR_CODE_OFFLINE
import com.andyha.coreui.manager.ApiErrorHandler.Companion.ERROR_CODE_TIMEOUT
import com.andyha.coreutils.setOnDebounceClick
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.reflect.ParameterizedType

abstract class BaseBottomSheetDialogFragment<VB : ViewBinding, VM : BaseViewModel>(private val viewBindingFactory: (LayoutInflater) -> VB) :
    BottomSheetDialogFragment() {

    var titleRes: Int? = null
    var onCancel: (() -> Unit)? = null
    var needLoading: Boolean = false
    var useCommonTitle: Boolean = true
    var onDismiss: (() -> Unit)? = null
    var percentHeight: Float = 1f

    protected var tvTitle: BaseTextView? = null

    private var progressDialog: ProgressDialog? = null
        get() {
            if (field == null) field = ProgressDialog()
            return field
        }

    val viewBinding: VB
        get() = _viewBinding!!
    private var _viewBinding: VB? = null

    protected val viewModel: VM by lazy {
        ViewModelProvider(this).get((this::class.java.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<VM>)
    }

    private var currentProgress: ProgressType? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (!useCommonTitle) {
            return viewBinding.root
        }

        val binding = BaseBottomSheetDialogBinding.inflate(LayoutInflater.from(context))

        tvTitle = binding.tvTitle
        tvTitle?.setCustomText(titleRes)

        binding.icClose.apply {
            expandClickArea()
            setOnDebounceClick {
                dismiss()
                onCancel?.invoke()
            }
        }

        val layoutParams = if (percentHeight == 1f) {
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
                .apply { weight = 1F }
        } else {
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                getPopupHeight(percent = percentHeight)
            ).apply { weight = 1F }
        }
        binding.root.addView(viewBinding.root, layoutParams)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (needLoading) {
            viewBinding.root.isVisible = false
            view.isVisible = false
            (view.parent as? View)?.isVisible = false
        }

        if (needLoading) {
            observeViewModelState(viewModel)
        }

        onFragmentCreated()
    }

    abstract fun onFragmentCreated()

    fun onDataLoaded() {
        if (needLoading) {
            val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_up)
            (view?.parent as? View)?.startAnimation(slideUpAnimation)
            viewBinding.root.isVisible = true
            view?.isVisible = true
            (view?.parent as? View)?.isVisible = true
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _viewBinding = viewBindingFactory(layoutInflater)
        return BaseBottomSheetDialog(requireContext(), needLoading)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onCancel?.invoke()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss?.invoke()
    }

    override fun onDestroy() {
        hideProgressAndRelease()
        super.onDestroy()
    }

    private fun hideProgressAndRelease() {
        progressDialog?.hideProgress()
        progressDialog = null
    }

    private fun observeViewModelState(viewModel: BaseViewModel) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewModelState.collect {
                    when (it) {
                        is ViewModelState.Loading -> {
                            showProgress(it.progress)
                        }
                        is ViewModelState.Idle -> {
                            hideProgress(it)
                        }
                        is ViewModelState.Error -> {
                            hideProgress(it)
                            displayError(it)
                        }
                    }
                }
            }
        }

    }

    open fun showProgress(progress: ProgressType) {
        when (progress) {
            is ProgressType.ProgressDialog -> {
                progressDialog?.showProgress(childFragmentManager)
            }
            is ProgressType.TransitionButton -> {
                view?.findViewById<TransitionButton>(progress.buttonId)?.startAnimation()
            }
            else -> {}
        }
        this.currentProgress = progress
    }

    open fun hideProgress(toState: ViewModelState) {
        when (currentProgress) {
            is ProgressType.ProgressDialog -> {
                hideProgressAndRelease()
            }

            is ProgressType.TransitionButton -> {
                view?.findViewById<TransitionButton>(
                    (currentProgress as ProgressType.TransitionButton).buttonId
                )?.let {
                    if (toState is ViewModelState.Error) {
                        it.stopAnimationToError()
                    } else {
                        it.stopAnimationToIdle()
                    }
                }
            }
            else -> {}
        }
    }

    open fun displayError(error: ViewModelState.Error) {
        Timber.e("displayError: $error")
        if (!error.shouldNotify) return
        // Show warning dialog for offline and timeout case
        when (error.code) {
            ERROR_CODE_OFFLINE -> {
                // Display offline error
            }
            ERROR_CODE_TIMEOUT -> {
                // Display timeout error
            }
            else -> {
                context?.showToast(error.error)
            }
        }
    }

    private fun getPopupHeight(percent: Float): Int =
        (DimensionUtils.getScreenHeight() * percent).toInt()

    protected fun setTitle(title: String) {
        tvTitle?.text = title
    }

    protected fun setTitle(@StringRes title: Int) {
        titleRes = title
        tvTitle?.setCustomText(title)
    }
}

private class BaseBottomSheetDialog(context: Context, private val needLoading: Boolean = false) :
    BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme) {


    override fun setContentView(view: View) {
        super.setContentView(view)
        if (!needLoading) {
            view.setOnGlobalLayoutListener {
                val bottomSheet = findViewById<View>(R.id.design_bottom_sheet)
                if (bottomSheet != null) {
                    val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        } else {
            val bottomSheet = findViewById<View>(R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }
}