package com.andyha.corenetwork.di

import com.google.gson.Gson
import com.andyha.corenetwork.ResponseConverter
import com.andyha.corenetwork.qualifier.Authenticator
import com.andyha.corenetwork.qualifier.ForLogging
import com.andyha.corenetwork.qualifier.ForRequestInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class AuthenticatorModule {

    @Singleton
    @Provides
    @Authenticator
    fun provideHttpClientForAuthenticator(
        @ForRequestInterceptor tokenInterceptor: Interceptor,
        @ForLogging logging: Interceptor?,
    ): OkHttpClient {
        val httpClient = OkHttpClient.Builder().apply {
            connectTimeout(15, TimeUnit.SECONDS)
            readTimeout(15, TimeUnit.SECONDS)
            writeTimeout(15, TimeUnit.SECONDS)
            addInterceptor(tokenInterceptor)
            logging?.let { addInterceptor(it) }
        }
        return httpClient.build()
    }

    @Singleton
    @Provides
    @Authenticator
    fun provideRetrofitForAuthenticator(
        @Authenticator baseUrl: String,
        gson: Gson,
        @Authenticator okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ResponseConverter(gson))
        .client(okHttpClient)
        .build()
}