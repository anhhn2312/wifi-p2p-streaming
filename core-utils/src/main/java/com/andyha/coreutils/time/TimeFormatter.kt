package com.andyha.coreutils.time

import android.content.Context
import android.text.format.DateFormat
import com.andyha.coreutils.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor


object TimeFormatter {

    private const val MINUTE = 60
    private const val HOUR = 60 * 60
    private const val DAY = 60 * 60 * 24

    private val cacheDateFormat: HashMap<String, SimpleDateFormat> by lazy { HashMap() }

    fun getDateFormat(pattern: String, local: Locale? = Locale.getDefault()): SimpleDateFormat {
        if (cacheDateFormat[pattern + local.toString()] == null) {
            val format = SimpleDateFormat(pattern, local)
            cacheDateFormat[pattern + local.toString()] = format
        }
        return cacheDateFormat[pattern + local.toString()]!!
    }

    fun timestampToDate(context: Context?, timestamp: Long): String? {
        val date = timeStampToDate(timestamp) ?: return ""
        return formatDate(date, TimeFormat.getTimeFormat(is24hFormat(context)).date)
    }

    fun timestampToWeekDay(context: Context?, timestamp: Long): String? {
        val date = timeStampToDate(timestamp) ?: return ""
        return formatDate(date, TimeFormat.getTimeFormat(is24hFormat(context)).weekDay)
    }

    fun timestampToHourMinute(context: Context?, timestamp: Long): String? {
        val date = timeStampToDate(timestamp) ?: return ""
        return formatDate(date, TimeFormat.getTimeFormat(is24hFormat(context)).hourMinute)
    }

    fun timestampToDayMonthYear(context: Context?, timestamp: Long): String? {
        val date = timeStampToDate(timestamp) ?: return ""
        return formatDate(date, TimeFormat.getTimeFormat(is24hFormat(context)).dayMonthYear)
    }

    fun formatTimeInterval(
        context: Context,
        range: Long?,
        needRoundMinuteLessHour: Boolean = false
    ): String {
        if (range == null) {
            return ""
        }

        return when {
            range < MINUTE -> {
                val second = range.toInt()
                context.resources.getQuantityString(R.plurals.time_range_seconds, second, second)
            }
            range < HOUR -> {
                val minute = floor(range.toDouble() / MINUTE).toInt()
                val minuteText = context.resources.getQuantityString(
                    R.plurals.time_range_minutes,
                    minute,
                    minute
                )

                if (needRoundMinuteLessHour) {
                    minuteText
                } else {
                    val second = (range % MINUTE).toInt()
                    if (second == 0) {
                        minuteText
                    } else {
                        val secondText = context.resources.getQuantityString(
                            R.plurals.time_range_seconds,
                            second,
                            second
                        )
                        "$minuteText $secondText"
                    }
                }
            }
            range < DAY -> {
                val hour = floor(range.toDouble() / HOUR).toInt()
                val hourText =
                    context.resources.getQuantityString(R.plurals.time_range_hour, hour, hour)

                val minute = floor((range % HOUR).toDouble() / MINUTE).toInt()
                if (minute == 0) {
                    hourText
                } else {
                    val minuteText =
                        context.resources.getQuantityString(
                            R.plurals.time_range_minutes,
                            minute,
                            minute
                        )
                    "$hourText $minuteText"
                }
            }
            else -> {
                val day = floor(range.toDouble() / DAY).toInt()
                context.resources.getQuantityString(R.plurals.time_range_day, day, day)
            }
        }
    }

    fun clear() { cacheDateFormat.clear() }

    private fun formatDate(source: Date?, format: String?): String? {
        if (source == null) {
            return null
        }
        if (format == null || format.isEmpty()) {
            return null
        }
        val sdf = getDateFormat(format)
        return sdf.format(source)
    }

    private fun timeStampToDate(timestamp: Long): Date? {
        return try {
            Date(timestamp)
        } catch (e: RuntimeException) {
            null
        }
    }

    //45 -> 00:45, 70 -> 01:10
    fun formatDurations(duration: Int): String {
        return if (duration < 60) {
            "00:${String.format("%02d", duration)}"
        } else {
            val min = duration / 60
            val second = duration - min * 60
            "${String.format("%02d", min)}:${String.format("%02d", second)}"
        }
    }

    private fun is24hFormat(context: Context?): Boolean {
        return true == context?.let { DateFormat.is24HourFormat(it) }
    }
}