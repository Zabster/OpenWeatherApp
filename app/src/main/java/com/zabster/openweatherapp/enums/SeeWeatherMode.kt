package com.zabster.openweatherapp.enums

enum class SeeWeatherMode(val key: Int) {
    FROM_CITY_NAME(0), FROM_LOCATION(1);

    companion object {
        fun getMode(id: Int) = values().find { it.key == id } ?: FROM_CITY_NAME
    }
}