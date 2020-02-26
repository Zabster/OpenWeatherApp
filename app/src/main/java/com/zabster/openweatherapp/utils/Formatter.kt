package com.zabster.openweatherapp.utils

import java.text.SimpleDateFormat
import java.util.*

object Formatter {

    fun simpleDateFormat(date: Date?): String {
        if (date == null) return ""
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        return formatter.format(date)
    }

}