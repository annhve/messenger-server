package com.deledzis.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    val ISO_8601_24H_FULL_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale("ru"))
    val ISO_24H_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("ru"))
    val DF_ONLY_DAY = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
    val DF_WEEK_DAY_TIME = SimpleDateFormat("EEEE, d MMMM, в HH:mm МСК", Locale("ru"))
    val DF_DAY_TIME_TIME = SimpleDateFormat("dd MMMM yyyy в HH:mm МСК", Locale("ru"))
    val RU_DATE_FORMAT = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
    val SIMPLE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale("ru"))
    val OUTPUT_SIMPLE_FORMAT = SimpleDateFormat("dd MMMM", Locale("ru"))
    val TIME_FORMAT = SimpleDateFormat("HH:mm", Locale("ru"))
    val MONTH_FORMAT = SimpleDateFormat("LLLL yyyy", Locale("ru"))
    val DAY_FORMAT = SimpleDateFormat("dd", Locale("ru"))

    fun getDate(year: Int, month: Int, day: Int, ignoreTime: Boolean = true): Date {
        val date = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }.time
        return if (ignoreTime) date.ignoreTime() else date
    }

    fun getDate(date: String, format: SimpleDateFormat = ISO_24H_FORMAT): Date =
        format.parse(date)

    fun getCurrentDate(ignoreTime: Boolean = true): Date = if (ignoreTime) {
        Calendar.getInstance().time.ignoreTime()
    } else {
        Calendar.getInstance().time
    }

    fun getDaysInMonth(day: Date): MutableList<Date?> {
        val dates = mutableListOf<Date?>()
        val cal = Calendar.getInstance().apply { time = day }
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, i)
            dates.add(cal.time)
        }

        supplementMonthDaysToInactiveDays(dates)

        return dates
    }

    private fun supplementMonthDaysToInactiveDays(month: MutableList<Date?>) {
        var firstDateWeekDay = Calendar
            .getInstance()
            .apply { time = month.first()!! }[Calendar.DAY_OF_WEEK] - 1
        if (firstDateWeekDay == 0) firstDateWeekDay = 7 // Saturday in Russia's metric system
        var lastDateWeekDay = Calendar
            .getInstance()
            .apply { time = month.last()!! }[Calendar.DAY_OF_WEEK] - 1
        if (lastDateWeekDay == 0) lastDateWeekDay = 7 // Saturday in Russia's metric system
        for (i in 1 until firstDateWeekDay) {
            month.add(0, null)
        }
        for (i in lastDateWeekDay until 7) {
            month.add(null)
        }
    }

    fun getDateWithOffset(
        fromDate: Date? = null,
        seconds: Int = 0,
        minutes: Int = 0,
        hours: Int = 0,
        days: Int = 0,
        weeks: Int = 0,
        months: Int = 0,
        years: Int = 0
    ): Date {
        val date = Calendar.getInstance()
        fromDate?.let { date.time = it }
        date.add(Calendar.SECOND, seconds)
        date.add(Calendar.MINUTE, minutes)
        date.add(Calendar.HOUR_OF_DAY, hours)
        date.add(Calendar.DATE, days)
        date.add(Calendar.WEEK_OF_YEAR, weeks)
        date.add(Calendar.MONTH, months)
        date.add(Calendar.YEAR, years)
        return date.time
    }
}