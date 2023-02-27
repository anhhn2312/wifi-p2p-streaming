package com.andyha.coreui.base.viewModel


sealed class ViewModelState {
    object Idle : ViewModelState()
    data class Loading(val progress: ProgressType) : ViewModelState()
    data class Error(
        val code: String? = null,
        val error: String,
        var shouldNotify: Boolean = false
    ) : ViewModelState()
}