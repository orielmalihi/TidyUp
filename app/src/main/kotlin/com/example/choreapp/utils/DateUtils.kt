package com.example.choreapp.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun getTodayDate(): String = dateFormat.format(Date())

    fun getDateString(date: Date): String = dateFormat.format(date)

    fun parseDate(dateString: String): Date? = try {
        dateFormat.parse(dateString)
    } catch (e: Exception) {
        null
    }

    fun isToday(dateString: String): Boolean = dateString == getTodayDate()

    fun getDaysDifference(date1: String, date2: String): Long {
        val d1 = parseDate(date1) ?: return 0
        val d2 = parseDate(date2) ?: return 0
        return (d2.time - d1.time) / (1000 * 60 * 60 * 24)
    }
}
