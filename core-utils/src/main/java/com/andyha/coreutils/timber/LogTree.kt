package com.andyha.coreutils.timber

import timber.log.Timber

class AppTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        val className = super.createStackElementTag(element)?.split("$")?.get(0)
        return "Timber\uD83D\uDC49\uD83D\uDC49($className.kt:${element.lineNumber})#${element.methodName}"
    }
}