package com.andyha.coreui.base.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.andyha.coredata.manager.NetworkState
import com.andyha.coreextension.*
import com.andyha.coreui.R
import com.andyha.coreui.base.activity.BaseActivity
import com.andyha.coreui.base.ui.widget.*
import com.andyha.coreui.base.ui.widget.progress.ProgressDialog
import com.andyha.coreui.base.ui.widget.transitionbutton.TransitionButton
import com.andyha.coreui.base.viewModel.BaseViewModel
import com.andyha.coreui.base.viewModel.ProgressType
import com.andyha.coreui.base.viewModel.ViewModelState
import com.andyha.coreui.base.viewModel.ViewModelState.Idle
import com.andyha.coreui.base.viewModel.ViewModelState.Loading
import com.andyha.coreui.manager.ApiErrorHandler.Companion.ERROR_CODE_OFFLINE
import com.andyha.coreui.manager.ApiErrorHandler.Companion.ERROR_CODE_TIMEOUT
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.reflect.ParameterizedType


abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel>(val bindingFactory: (LayoutInflater) -> VB) :
    HiltFragment() {

    private var _viewBinding: VB? = null

    fun viewBinding(): VB {
        if (_viewBinding == null) throw NullPointerException("viewBinding is null!!")
        return _viewBinding!!
    }

    protected val viewModel: VM by lazy {
        ViewModelProvider(this).get((this::class.java.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<VM>)
    }

    private val mToolbarConfiguration by lazy { getToolbarConfiguration() }

    private var progressDialog: ProgressDialog? = null
        get() {
            if (field == null) field = ProgressDialog()
            return field
        }

    private var refreshLayout: SwipeRefreshLayout? = null

    private var currentProgress: ProgressType? = null

    // Enable back pressing in 600ms after the current fragment's view is created
    private var isBackPressedEnabled = false

    private val backPressCallback: OnBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isBackPressedEnabled) return
                onBackPressed()
            }
        }
    }

    open fun getToolbarConfiguration() =
        ToolbarConfiguration.Builder()
            .setDefaultToolbarEnabled(true)
            .setHasBackButton(true)
            .setHasRefreshLayout(true)
            .build()

    //region inflate layout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = bindingFactory(layoutInflater)
        return inflateView()
    }

    private fun inflateView(): View {
        val root = when {
            mToolbarConfiguration.hasRefreshLayout -> {
                inflateRefreshLayoutContainer()
            }
            mToolbarConfiguration.isDefaultToolbarEnabled -> {
                inflateToolbarLayoutContainer()
            }
            else -> {
                setupToolbar(viewBinding().root as? ViewGroup)
                viewBinding().root
            }
        }
        root.hideKeyBoardWhenTouchOutside()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModelState(viewModel)
        observeLocaleChange()
        observeNetworkChange()

        onFragmentCreated(view, savedInstanceState)

        Handler().postDelayed({
            isBackPressedEnabled = true
            backPressCallback.isEnabled = shouldHandleOnBackPressed()
        }, BACK_PRESS_DELAY)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return getPopupToDefaultAnimation(enter, listDestinationToPop())
            ?: super.onCreateAnimation(transit, enter, nextAnim)
    }

    private fun inflateRefreshLayoutContainer(): View {
        val root = View.inflate(context, getBaseRefreshFragmentLayout(), null) as ViewGroup
        root.findViewById<FrameLayout>(R.id.base_contents).apply { addView(viewBinding().root) }

        setupBackground(root)
        setupToolbar(root)

        refreshLayout = root.findViewById(R.id.base_refresh_layout)
        refreshLayout?.let {
            it.isEnabled = mToolbarConfiguration.hasRefreshLayout
            it.setColorSchemeColors(requireContext().getColorAttr(R.attr.colorAccent))
            it.setOnRefreshListener {
                onRefreshView()
                it.isRefreshing = false
            }
        }
        return root
    }

    private fun inflateToolbarLayoutContainer(): View {
        val resLayout = getBaseFragmentLayout()
        val root = View.inflate(context, resLayout, null) as ViewGroup
        root.findViewById<FrameLayout>(R.id.base_contents).apply { addView(viewBinding().root) }

        setupBackground(root)
        setupToolbar(root)

        return root
    }

    private fun setupBackground(view: View) {
        if ((activity as? BaseActivity<*>)?.shouldShowFullscreen() == true) {
            view.setPaddingTop(requireContext().getStatusBarHeight())
        }
        if (hasBackground()) {
            view.setBackgroundResource(R.drawable.bg_app)
        }
    }

    private fun setupToolbar(view: ViewGroup? = null) {
        val defaultToolBar = view?.findViewById<BaseToolbar>(R.id.baseHeaderBar)

        if (!mToolbarConfiguration.isDefaultToolbarEnabled) {
            view?.removeView(defaultToolBar)
        }

        val toolBar = getToolbar(view)

        toolBar?.apply {
            if (mToolbarConfiguration.hasBackButton) setupBackButton()

            mToolbarConfiguration.title?.let { setTitleText(it) }
            mToolbarConfiguration.titleStr?.let { setTitleText(it) }

            if (mToolbarConfiguration.titleStyle == TitleStyle.BIG) setBigTitle()

            mToolbarConfiguration.menuIcon?.let {
                setIconEnd(it)
                setVisibilityIconEnd(mToolbarConfiguration.menuVisibility)
                setOnClickIconEnd { onClickMenuButton() }
                setOnClickTextEnd { onClickTextMenu() }
            }
            mToolbarConfiguration.menuText?.let {
                setTextIconEnd(it)
                setVisibilityTextIconEnd(View.VISIBLE)
            }
            hideKeyBoardWhenTouchOutside()
        }
    }

    protected open fun setFitSystemWindow(
        insetView: View?,
        insetLeft: Int? = null,
        insetTop: Int? = null,
        insetRight: Int? = null,
        insetBottom: Int? = null
    ) {
        view?.setOnApplyWindowInsetsListener { v, insets ->
            insets.replaceSystemWindowInsets(
                insetLeft ?: insets.systemWindowInsetLeft,
                insetTop ?: insets.systemWindowInsetTop,
                insetRight ?: insets.systemWindowInsetRight,
                insetBottom ?: insets.systemWindowInsetBottom
            )
        }
        insetView?.fitsSystemWindows = true
    }

    private fun observeViewModelState(viewModel: BaseViewModel) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewModelState.collect {
                    when (it) {
                        is Loading -> showProgress(it.progress)
                        is Idle -> hideProgress(it)
                        is ViewModelState.Error -> {
                            hideProgress(it)
                            displayError(it)
                        }
                    }
                }
            }
        }
    }

    private fun observeLocaleChange() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                configurationManager.currentLocale
                    .filterNotNull()
                    .distinctUntilChanged()
                    .collectLatestSafely { onLocaleChanged() }
            }
        }
    }

    private fun observeNetworkChange() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkConnectionManager.networkState
                    .collectLatestSafely { onNetworkChange(it) }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setupOnBackPressed()
    }

    override fun onDestroyView() {
        releaseRvAdapter()
        _viewBinding = null
        refreshLayout = null
        hideProgressAndRelease()
        super.onDestroyView()
    }

    private fun BaseToolbar.setupBackButton() {
        setNavigationIcon(R.drawable.ic_back_white)
        setNavigationOnClickListener {
            if (!isBackPressedEnabled) return@setNavigationOnClickListener

            if (shouldHandleOnBackPressed()) {
                onBackPressed(true)
            } else {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun getToolbar(viewGroup: View? = this.view): BaseToolbar? {
        return if (!mToolbarConfiguration.isDefaultToolbarEnabled && mToolbarConfiguration.customToolbar != null) {
            mToolbarConfiguration.customToolbar
        } else {
            viewGroup?.findViewById(R.id.baseHeaderBar)
        }
    }

    abstract fun onFragmentCreated(view: View, savedInstanceState: Bundle?)

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
                showSnackBar(error.error)
            }
        }
    }

    private fun setupOnBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(this, backPressCallback)
    }

    open fun onBackPressed(navigateUp: Boolean = false) {
        val isNavHostFragmentExisted =
            requireActivity().supportFragmentManager.fragments.any { it is NavHostFragment }
        if (isNavHostFragmentExisted && navigateUp && findNavController().navigateUp()) {
            return
        }
        if (isNavHostFragmentExisted && findNavController().popBackStack()) {
            return
        }

        if ((requireActivity() as? BaseActivity<*>)?.shouldSupportSharedElementTransition() == true) {
            requireActivity().finishAfterTransition()
            return
        }

        requireActivity().finish()
    }
    //endregion OnBackPressed

    open fun onLocaleChanged() {
        Timber.d("onLanguageChange: ${this.javaClass}")
        getToolbar()?.refresh()
        (activity as? BaseActivity<*>)?.onLocaleChanged()
    }

    open fun onNetworkChange(networkState: NetworkState) {
        Timber.d("NetworkDebug: $networkState")
        view?.findViewById<NoInternetBanner>(R.id.noInternetBanner)
            ?.toggleShow(this, networkState == NetworkState.UNAVAILABLE)
    }

    open fun showProgress(progress: ProgressType) {
        when (progress) {
            is ProgressType.ProgressDialog -> {
                progressDialog?.showProgress(childFragmentManager)
            }
            is ProgressType.SwipeRefresh -> {
                refreshLayout?.isRefreshing = true
            }
            is ProgressType.TransitionButton -> {
                view?.findViewById<TransitionButton>(progress.buttonId)?.startAnimation()
            }
            is ProgressType.ShimmerView -> {
                view?.findViewById<ShimmerView>(progress.viewId)?.let {
                    it.isVisible = true
                    it.startShimmer()
                }
            }
            else -> {}
        }
        this.currentProgress = progress
    }

    open fun hideProgress(toState: ViewModelState) {
        hideProgressAndRelease()
        when (currentProgress) {
            is ProgressType.ProgressDialog,
            is ProgressType.SwipeRefresh -> refreshLayout?.isRefreshing = false

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

            is ProgressType.ShimmerView -> {
                view?.findViewById<ShimmerView>(
                    (currentProgress as ProgressType.ShimmerView).viewId
                )?.let {
                    it.stopShimmer()
                    it.isVisible = false
                }
            }

            else -> refreshLayout?.isRefreshing = false
        }
    }

    private fun releaseRvAdapter() {
        // Fix leak adapter issue.
        (_viewBinding?.root as? ViewGroup)?.apply {
            for (i in 0 until childCount) {
                (getChildAt(i) as? RecyclerView)?.adapter = null
            }
        }
    }

    private fun hideProgressAndRelease() {
        if (true == progressDialog?.hideProgress())
            progressDialog = null
    }

    // Get the base fragment layout to inflate
    open fun getBaseFragmentLayout(): Int = R.layout.fragment_base

    // Get the base refresh fragment layout to inflate
    open fun getBaseRefreshFragmentLayout(): Int = R.layout.fragment_base_refresh_layout

    // Determine if this fragment should handle manual back press
    open fun shouldHandleOnBackPressed(): Boolean = false

    // Called when click on right menu button on the toolbar
    open fun onClickMenuButton() {}

    // Called when click on right text menu on the toolbar
    open fun onClickTextMenu() {}

    // Called when swipe refresh layout is pulled
    open fun onRefreshView() {}

    // Called when click on retry button in ConnectionErrorView
    open fun onRetry() {}

    // Specify list of popped out fragments when creating popupTo animation
    open fun listDestinationToPop() = arrayListOf<Int>()

    // Determine if fragment should has background or not
    open fun hasBackground() = (activity as? BaseActivity<*>)?.hasBackground() ?: false

    companion object {
        // To prevent user from quickly pressing back button which causes empty screen on previous fragment.
        const val BACK_PRESS_DELAY = 600L
    }
}