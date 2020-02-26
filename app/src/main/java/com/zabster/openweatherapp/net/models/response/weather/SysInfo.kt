package com.zabster.openweatherapp.net.models.response.weather

data class SysInfo(
    val type: Int?,
    val id: Long?,
    val message: Double?,
    val country: String?,
    val sunrise: Long?,
    val sunset: Long?
)