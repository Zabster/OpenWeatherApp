package com.zabster.openweatherapp.db.models

import android.content.ContentValues
import android.database.Cursor
import android.provider.BaseColumns
import java.lang.Exception
import java.util.*

data class WeatherDbModel(

    val id: Int,

    val title: String,
    val description: String,

    val sunrise: Date?,
    val sunset: Date?,

    val windSpeed: Double,
    val windDeg: Double,

    val temp: Double,
    val humidity: Int,
    val pressure: Int,

    val insertDate: Date

)

fun WeatherDbModel.toContentValues(): ContentValues = ContentValues().apply {
    put(WeatherEntry.COLUMN_TITLE, title)
    put(WeatherEntry.COLUMN_DESC, description)
    put(WeatherEntry.COLUMN_TEMPERATURE, temp)
    put(WeatherEntry.COLUMN_PRESSURE, pressure)
    put(WeatherEntry.COLUMN_HUMIDITY, humidity)
    put(WeatherEntry.COLUMN_WIND_DEG, windDeg)
    put(WeatherEntry.COLUMN_WIND_SPEED, windSpeed)
    put(WeatherEntry.COLUMN_SUNSET, sunset?.time)
    put(WeatherEntry.COLUMN_SUNRISE, sunrise?.time)
    put(WeatherEntry.COLUMN_INSERT_DATE, insertDate.time)
}

fun Cursor.toDBModel(): WeatherDbModel? {
    try {
        return WeatherDbModel(
            id = getInt(getColumnIndexOrThrow(BaseColumns._ID)),
            title = getString(getColumnIndexOrThrow(WeatherEntry.COLUMN_TITLE)),
            description = getString(getColumnIndexOrThrow(WeatherEntry.COLUMN_DESC)),
            humidity = getInt(getColumnIndexOrThrow(WeatherEntry.COLUMN_HUMIDITY)),
            pressure = getInt(getColumnIndexOrThrow(WeatherEntry.COLUMN_PRESSURE)),
            temp = getDouble(getColumnIndexOrThrow(WeatherEntry.COLUMN_TEMPERATURE)),
            sunrise = Date(getLong(getColumnIndexOrThrow(WeatherEntry.COLUMN_SUNRISE))),
            sunset = Date(getLong(getColumnIndexOrThrow(WeatherEntry.COLUMN_SUNSET))),
            windSpeed = getDouble(getColumnIndexOrThrow(WeatherEntry.COLUMN_WIND_SPEED)),
            windDeg = getDouble(getColumnIndexOrThrow(WeatherEntry.COLUMN_WIND_DEG)),
            insertDate = Date(getLong(getColumnIndexOrThrow(WeatherEntry.COLUMN_INSERT_DATE)))
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

object WeatherEntry : BaseColumns {
    const val TABLE_NAME = "weather"

    const val COLUMN_SUNRISE = "sunrise"
    const val COLUMN_SUNSET = "sunset"
    const val COLUMN_WIND_SPEED = "wind_speed"
    const val COLUMN_WIND_DEG = "wind_deg"
    const val COLUMN_HUMIDITY = "humidity"
    const val COLUMN_PRESSURE = "pressure"
    const val COLUMN_TEMPERATURE = "temp"
    const val COLUMN_TITLE = "title"
    const val COLUMN_DESC = "desc"
    const val COLUMN_INSERT_DATE = "insert_date"

    val COLUMN_ALL = arrayOf(
        BaseColumns._ID,
        COLUMN_TITLE,
        COLUMN_DESC,
        COLUMN_HUMIDITY,
        COLUMN_PRESSURE,
        COLUMN_SUNRISE,
        COLUMN_SUNSET,
        COLUMN_TEMPERATURE,
        COLUMN_WIND_DEG,
        COLUMN_WIND_SPEED,
        COLUMN_INSERT_DATE
    )
}
