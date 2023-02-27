package com.andyha.p2p.streaming.client.manager

import com.andyha.coredata.manager.SessionManager
import com.andyha.coredata.manager.SessionState
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject


class StubSessionManager @Inject constructor(
) : SessionManager {
    override val sessionState = MutableSharedFlow<SessionState>(replay = 1)

    override fun onTokenChanged(accessToken: String?, refreshToken: String?, idToken: String?) {

    }

    override fun signOut() {

    }

    override fun isLoggedIn(): Boolean = false
}