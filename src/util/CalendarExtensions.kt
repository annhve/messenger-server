package com.deledzis.util

import java.util.*

fun Calendar.compareIgnoreTime(other: Calendar): Int {
    val isEqualYear = this.get(Calendar.YEAR)
        .compareTo(other.get(Calendar.YEAR))
    if (isEqualYear != 0) {
        return isEqualYear
    }
    val isEqualMonth = this.get(Calendar.MONTH)
        .compareTo(other.get(Calendar.MONTH))
    if (isEqualMonth != 0) {
        return isEqualMonth
    }
    return get(Calendar.DAY_OF_MONTH)
        .compareTo(other.get(Calendar.DAY_OF_MONTH))
}

fun Calendar.dayBegin() = this.also {
    it.hour = 0
    it.minute = 0
    it.second = 0
}

fun Calendar.dayEnd() = this.also {
    it.hour = 23
    it.minute = 59
    it.second = 59
    it.millisecond = 999
}

var Calendar.year: Int
    set(value) {
        this.set(Calendar.YEAR, value)
    }
    get() = this.get(Calendar.YEAR)

var Calendar.month: Int
    set(value) {
        this.set(Calendar.MONTH, value)
    }
    get() = this.get(Calendar.MONTH)

var Calendar.day: Int
    set(value) {
        this.set(Calendar.DAY_OF_MONTH, value)
    }
    get() = this.get(Calendar.DAY_OF_MONTH)

var Calendar.hour: Int
    set(value) {
        this.set(Calendar.HOUR_OF_DAY, value)
    }
    get() = this.get(Calendar.HOUR_OF_DAY)

var Calendar.minute: Int
    set(value) {
        this.set(Calendar.MINUTE, value)
    }
    get() = this.get(Calendar.MINUTE)

var Calendar.second: Int
    set(value) {
        this.set(Calendar.SECOND, value)
    }
    get() = this.get(Calendar.SECOND)

var Calendar.millisecond: Int
    set(value) {
        this.set(Calendar.MILLISECOND, value)
    }
    get() = this.get(Calendar.MILLISECOND)
