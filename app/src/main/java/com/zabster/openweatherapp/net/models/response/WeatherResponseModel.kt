package com.zabster.openweatherapp.net.models.response

import com.zabster.openweatherapp.db.models.WeatherDbModel
import com.zabster.openweatherapp.net.models.response.weather.*
import java.util.*

data class WeatherResponseModel(
    val coord: Coordinats?,
    val weather: List<Weather>?,
    val base: String?,
    val main: MainInfo?,
    val wind: Wind?,
    val clouds: Clouds?,
    val dt: Long?,
    val sys: SysInfo?,
    val timeZone: Long?,
    val id: Long?,
    val name: String?,
    val cod: Int?
)

fun WeatherResponseModel?.toDbModel(): WeatherDbModel? =
    if (this == null) null else {
        val weather = weather?.firstOrNull()
        val sunrise = if (sys?.sunrise != null) Date(sys.sunrise) else null
        val sunset = if (sys?.sunset != null) Date(sys.sunset) else null
        WeatherDbModel(
            id = -1,
            title = weather?.main ?: "",
            description = weather?.description ?: "",
            humidity = main?.humidity ?: Int.MIN_VALUE,
            pressure = main?.pressure ?: Int.MIN_VALUE,
            temp = main?.temp ?: Double.NaN,
            sunrise = sunrise,
            sunset = sunset,
            windSpeed = wind?.speed ?: Double.NaN,
            windDeg = wind?.deg ?: Double.NaN,
            insertDate = Date()
        )
    }
