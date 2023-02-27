package com.andyha.corenetwork.interceptor

import com.andyha.corenetwork.config.NetworkConfigConstants.DEFAULT_CONNECT_TIMEOUT
import com.andyha.corenetwork.config.NetworkConfigConstants.DEFAULT_READ_TIMEOUT
import com.andyha.corenetwork.config.NetworkConfigConstants.DEFAULT_WRITE_TIMEOUT
import com.andyha.corenetwork.qualifier.ConnectTimeout
import com.andyha.corenetwork.qualifier.ReadTimeout
import com.andyha.corenetwork.qualifier.WriteTimeout
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation
import java.io.IOException
import java.util.concurrent.TimeUnit


internal class TimeoutInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        val connectTimeout =
            (request.tag(Invocation::class.java)?.method())?.getAnnotation(ConnectTimeout::class.java)
        val readTimeout =
            (request.tag(Invocation::class.java)?.method())?.getAnnotation(ReadTimeout::class.java)
        val writeTimeout =
            (request.tag(Invocation::class.java)?.method())?.getAnnotation(WriteTimeout::class.java)

        return chain
            .withConnectTimeout(
                connectTimeout?.duration ?: DEFAULT_CONNECT_TIMEOUT.toInt(),
                connectTimeout?.unit ?: TimeUnit.SECONDS
            )
            .withReadTimeout(
                readTimeout?.duration ?: DEFAULT_READ_TIMEOUT.toInt(),
                readTimeout?.unit ?: TimeUnit.SECONDS
            )
            .withWriteTimeout(
                writeTimeout?.duration ?: DEFAULT_WRITE_TIMEOUT.toInt(),
                writeTimeout?.unit ?: TimeUnit.SECONDS
            )
            .proceed(request)
    }
}