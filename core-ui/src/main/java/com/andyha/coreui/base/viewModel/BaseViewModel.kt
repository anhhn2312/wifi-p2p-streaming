package com.andyha.coreui.base.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andyha.coreui.base.viewModel.ProgressType.NoProgress
import com.andyha.coreui.base.viewModel.ProgressType.ProgressDialog
import com.andyha.coreui.base.viewModel.ViewModelState.Idle
import com.andyha.coreui.manager.ApiErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var errorHandler: ApiErrorHandler

    val viewModelState = MutableSharedFlow<ViewModelState>(replay = 1)

    open fun showProgress(progress: ProgressType) {
        viewModelState.tryEmit(ViewModelState.Loading(progress))
    }

    open fun hideProgress() {
        viewModelState.tryEmit(Idle)
    }

    protected fun <T> Flow<T>.justLaunch(action: suspend (T) -> Unit) {
        this.let { flow -> viewModelScope.launch{ justLaunch(flow).collect(action) } }
    }

    protected fun <T> justLaunch(
        flow: Flow<T>,
        progress: ProgressType = ProgressDialog,
        onApiError: ((ViewModelState.Error) -> Unit)? = null
    ): Flow<T> {
        return flow
            .flowOn(Dispatchers.IO)
            .onStart { showProgress(progress) }
            .onCompletion { hideProgress() }
            .onEach { hideProgress() }
            .catch { handleError(it, progress != NoProgress).let { onApiError?.invoke(it) } }
    }

    fun handleError(throwable: Throwable, shouldNotify: Boolean = true): ViewModelState.Error {
        hideProgress()
        val error =
            errorHandler.handleError(throwable).apply { this.shouldNotify = shouldNotify }
        viewModelState.tryEmit(error)
        return error
    }
}