package com.deledzis.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.formatDate(format: DateTimeFormatter = DateUtils.DATE_TIME_FULL_FORMAT): String =
    format.format(this)