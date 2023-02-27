package com.andyha.corenetwork.interceptor

import com.andyha.corenetwork.config.TokenRefresher
import com.andyha.corenetwork.config.NetworkConfigConstants.AUTHORIZATION
import com.andyha.corenetwork.config.NetworkConfigConstants.BEARER
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class AutoAuthenticator constructor(
    private val tokenRefresher: TokenRefresher
) : Authenticator {

    // Store the number of token refreshing count corresponding with the request url
    private val tokenRefreshCountMap = mutableMapOf<String, Int>()

    override fun authenticate(route: Route?, response: Response): Request? {

        val requestUrl = response.request.url.toString()

        // Max token refreshing count = MAX_TOKEN_REFRESH_COUNT
        if ((tokenRefreshCountMap[requestUrl] ?: 0) >= MAX_TOKEN_REFRESH_COUNT) {
            tokenRefreshCountMap.remove(requestUrl)
            return null
        }

        tokenRefreshCountMap[requestUrl] = (tokenRefreshCountMap[requestUrl] ?: 0) + 1

        // do refresh token
        return tokenRefresher.refreshToken()?.let { newToken ->
            response.request.newBuilder()
                // use the new access token to build a new request
                .header(AUTHORIZATION, "$BEARER $newToken")
                .build()
        }
    }

    companion object{
        const val MAX_TOKEN_REFRESH_COUNT = 3
    }
}