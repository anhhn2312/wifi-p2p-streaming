package com.andyha.coreutils.time

import java.util.*


abstract class TimeFormat {

    companion object {

        fun getTimeFormat(
            is24HourFormat: Boolean? = false,
            locale: Locale = Locale.getDefault()
        ): TimeFormat {
            return when (locale.language) {
                "vi" -> VietNamTimeFormat(is24HourFormat)
                else -> UsTimeFormat(is24HourFormat)
            }
        }
    }

    abstract val date: String
    abstract val weekDay: String
    abstract val dayMonthYear: String
    abstract val hourMinute: String
}