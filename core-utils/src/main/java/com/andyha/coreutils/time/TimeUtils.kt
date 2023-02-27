package com.andyha.coreutils.time

import java.util.*


object TimeUtils {

    fun isToday(time: Long): Boolean {
        val today = Calendar.getInstance()
        val timeCheck = Calendar.getInstance().apply {
            timeInMillis = time
        }

        return today.get(Calendar.YEAR) == timeCheck.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == timeCheck.get(
            Calendar.DAY_OF_YEAR
        )
    }

    fun isThisWeek(time: Long): Boolean {
        val today = Calendar.getInstance()
        val timeCheck = Calendar.getInstance().apply {
            timeInMillis = time
        }

        return today.get(Calendar.YEAR) == timeCheck.get(Calendar.YEAR) && today.get(Calendar.WEEK_OF_YEAR) == timeCheck.get(
            Calendar.WEEK_OF_YEAR
        )
    }

    fun isLastWeek(time: Long): Boolean {
        val today = Calendar.getInstance()
        val timeCheck = Calendar.getInstance().apply {
            timeInMillis = time
        }

        return today.get(Calendar.YEAR) == timeCheck.get(Calendar.YEAR) && today.get(Calendar.WEEK_OF_YEAR) - 1 == timeCheck.get(
            Calendar.WEEK_OF_YEAR
        ) - 1
    }

    fun isThisMonth(time: Long): Boolean {
        val today = Calendar.getInstance()
        val timeCheck = Calendar.getInstance().apply {
            timeInMillis = time
        }

        return today.get(Calendar.YEAR) == timeCheck.get(Calendar.YEAR) && today.get(Calendar.MONTH) == timeCheck.get(
            Calendar.MONTH
        )
    }

    private fun isThisYear(time: Long): Boolean {
        val today = Calendar.getInstance()
        val timeCheck = Calendar.getInstance().apply {
            timeInMillis = time
        }

        return today.get(Calendar.YEAR) == timeCheck.get(Calendar.YEAR)
    }
}