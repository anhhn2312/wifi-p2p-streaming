package com.andyha.coredata.manager

import kotlinx.coroutines.flow.SharedFlow

interface SessionManager {
    val sessionState: SharedFlow<SessionState>

    fun onTokenChanged(accessToken: String?, refreshToken: String?, idToken: String?)
    fun isLoggedIn(): Boolean
    fun signOut()
}

enum class SessionState {
    LoggedIn, LoggedOut
}