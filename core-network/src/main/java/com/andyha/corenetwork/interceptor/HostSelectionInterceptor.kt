package com.andyha.corenetwork.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.util.regex.Pattern


class HostSelectionInterceptor constructor(
    private val baseUrl: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val urlMatcher = Pattern.compile("^(https*)://([\\w.\\-]+):*(.*)/").matcher(baseUrl)

        if (urlMatcher.find()) {
            val urlBuilder = request.url.newBuilder()

            urlMatcher.group(1)?.let { urlBuilder.scheme(it) }

            urlMatcher.group(2)?.let { urlBuilder.host(it) }

            urlMatcher.group(3)?.toIntOrNull()?.let { urlBuilder.port(it) }

            request = request.newBuilder()
                .url(urlBuilder.build())
                .build()
        }

        return chain.proceed(request)
    }
}