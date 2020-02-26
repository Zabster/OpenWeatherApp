package com.zabster.openweatherapp.net.models.response.weather

data class MainInfo(
    val temp: Double?,
    val feels_like: Double?,
    val temp_min: Double?,
    val temp_max: Double?,
    val pressure: Int?,
    val humidity: Int?
)