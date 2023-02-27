package com.andyha.coreui.base.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.andyha.coreextension.hideKeyBoardWhenTouchOutside
import com.andyha.coreui.R
import com.andyha.coreui.base.ui.widget.MaxHeightLinearLayout
import com.andyha.coreui.base.ui.widget.progress.ProgressDialog
import com.andyha.coreui.base.ui.widget.showToast
import com.andyha.coreui.base.ui.widget.transitionbutton.TransitionButton
import com.andyha.coreui.base.viewModel.BaseViewModel
import com.andyha.coreui.base.viewModel.ProgressType
import com.andyha.coreui.base.viewModel.ViewModelState
import com.andyha.coreui.manager.ApiErrorHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.roundToInt


abstract class BaseDialogFragment<VB : ViewBinding>(private val viewBindingFactory: (LayoutInflater) -> VB) :
    DialogFragment() {

    val viewBinding: VB
        get() = _viewBinding!!
    private var _viewBinding: VB? = null

    private var currentProgress: ProgressType? = null

    open fun getViewModel(): BaseViewModel? = null

    private var progressDialog: ProgressDialog? = null
        get() {
            if (field == null) field = ProgressDialog()
            return field
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _viewBinding = viewBindingFactory(layoutInflater)
        val dialog = Dialog(ContextThemeWrapper(requireActivity(), R.style.DialogAnimation))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupDialogStyle()
    }

    protected open fun setupDialogStyle() {
        dialog?.window?.apply {
            val displayRectangle = Rect()
            decorView.getWindowVisibleDisplayFrame(displayRectangle)
            attributes = attributes.apply { width = (displayRectangle.width() * 0.95f).roundToInt() }
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val rootView = viewBinding.root
            if (rootView is MaxHeightLinearLayout) {
                val maxHeight = (displayRectangle.height() * 0.80f).roundToInt()
                rootView.setMaxHeightPx(maxHeight)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding.root.hideKeyBoardWhenTouchOutside()
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel()?.let { observeViewModelState(it) }
    }

    private fun observeViewModelState(viewModel: BaseViewModel) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.viewModelState.collect{
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
            ApiErrorHandler.ERROR_CODE_OFFLINE -> {
                //Display offline error
            }
            ApiErrorHandler.ERROR_CODE_TIMEOUT -> {
                // Display timeout error
            }
            else -> {
                context?.showToast(error.error)
            }
        }
    }

    private fun hideProgressAndRelease() {
        progressDialog?.hideProgress()
        progressDialog = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}