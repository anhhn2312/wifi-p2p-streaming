package com.andyha.p2p.streaming.server.manager

import com.andyha.coreui.base.viewModel.ViewModelState
import com.andyha.coreui.manager.ApiErrorHandler
import javax.inject.Inject


class StubApiErrorHandler @Inject constructor(
) : ApiErrorHandler {

    override fun handleError(throwable: Throwable): ViewModelState.Error {
        return ViewModelState.Error(error = "stub", shouldNotify = false)
    }
}