package com.andyha.coretesting.sharepreference

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.andyha.coredata.storage.preference.AppSharedPreference
import com.andyha.coretesting.sharepreference.internal.ContextMock
import com.andyha.coretesting.sharepreference.internal.SPContextWrapperMock
import com.andyha.coretesting.sharepreference.internal.SharedPreferencesFactory
import io.mockk.every
import io.mockk.mockkStatic
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


class SharePreferenceMocker {
    private var isThreadSafe = false

    init {
        mockkStatic(PreferenceManager::class)
        every { PreferenceManager.getDefaultSharedPreferences(any()) } returns createSharedPreferencesInternal()
    }

    fun mockAppSharedPreference(context: Application): AppSharedPreference {
        whenever(context.packageName) doReturn "com.andyha.corearchitecture.app"
        return AppSharedPreference(context, mock())
    }

    private fun createSharedPreferencesInternal(): SharedPreferences {
        return createFactory().create()
    }

    private fun createFactory(): SharedPreferencesFactory {
        return SharedPreferencesFactory(isThreadSafe)
    }

    fun setThreadSafe(isThreadSafe: Boolean): SharePreferenceMocker {
        this.isThreadSafe = isThreadSafe
        return this
    }

    private fun createContext(): Context {
        return SPContextWrapperMock(ContextMock(), createFactory())
    }

    private fun wrapContext(context: Context): Context {
        return SPContextWrapperMock(context, createFactory())
    }
}