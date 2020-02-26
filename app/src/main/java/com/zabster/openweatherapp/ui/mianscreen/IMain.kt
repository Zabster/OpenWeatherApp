package com.zabster.openweatherapp.ui.mianscreen

import com.zabster.openweatherapp.db.models.WeatherDbModel
import com.zabster.openweatherapp.net.models.errors.ErrorModel

interface IMain {

    // set loading state
    fun startLoading()
    fun stopLoading()

    // check location perm
    fun checkPermissions()
    fun checkEnableLocation()

    // set main info
    fun setInfo(haveError: Boolean, error: ErrorModel?, model: WeatherDbModel?)

    // set other errors
    fun showNoNetworkInfo()
    fun showNoLocalInfo()
    fun showNoHaveLocation()
    fun showEmptyCityNameError()

}