package com.zabster.openweatherapp.utils.other

import android.content.Context
import android.content.SharedPreferences
import com.zabster.openweatherapp.enums.SeeWeatherMode

private const val SHARED_NAME = "com.zabster.openweatherapp.shared"

class SharedPreferenceHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)

    private fun putInt(key: String, value: Int?, defaultValue: Int = -1) {
        sharedPreferences.edit()
            .putInt(key, value ?: defaultValue)
            .apply()
    }

    private fun getInt(key: String, defaultValue: Int = -1) =
        sharedPreferences.getInt(key, defaultValue)

    private fun putString(key: String, value: String?, defaultValue: String = "") {
        sharedPreferences.edit()
            .putString(key, value ?: defaultValue)
            .apply()
    }

    private fun getString(key: String, defaultValue: String = "") =
        sharedPreferences.getString(key, defaultValue) ?: defaultValue

    // help func
    fun getSeeMode(): Int = getInt(SharedKeys.SEE_MODE.key, SeeWeatherMode.FROM_CITY_NAME.key)

    fun setSeeMode(mode: SeeWeatherMode) {
        putInt(SharedKeys.SEE_MODE.key, mode.key, SeeWeatherMode.FROM_CITY_NAME.key)
    }

    fun getLastCityName(): String = getString(SharedKeys.LAST_SEE_CITY.key)

    fun setLastCityName(name: String) {
        putString(SharedKeys.LAST_SEE_CITY.key, name)
    }
}