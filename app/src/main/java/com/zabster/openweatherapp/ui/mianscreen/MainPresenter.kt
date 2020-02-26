package com.zabster.openweatherapp.ui.mianscreen

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import com.zabster.openweatherapp.app.App
import com.zabster.openweatherapp.db.actions.WeatherActions
import com.zabster.openweatherapp.enums.SeeWeatherMode
import com.zabster.openweatherapp.net.WeatherApi
import com.zabster.openweatherapp.net.models.errors.ErrorModel
import com.zabster.openweatherapp.net.models.response.Response
import com.zabster.openweatherapp.net.models.response.WeatherResponseModel
import com.zabster.openweatherapp.net.models.response.toDbModel
import com.zabster.openweatherapp.utils.ServiceUtils
import com.zabster.openweatherapp.utils.other.SharedPreferenceHelper
import kotlin.concurrent.thread

class MainPresenter(private val view: IMain) : LocationListener {

    private val db = WeatherActions(App.appDataBase) // db action instance
    private val applicationContext = App.appContext // application context
    private var loadTask: LoadInfoTask? = null // get info from api task
    private val sharedPreferenceHelper: SharedPreferenceHelper =
        SharedPreferenceHelper(applicationContext)

    val lastSeeMode: SeeWeatherMode
        get() = SeeWeatherMode.getMode(sharedPreferenceHelper.getSeeMode())

    val lastCityName: String
        get() = sharedPreferenceHelper.getLastCityName()

    val locPermission = ServiceUtils.getLocPermissions()

    // initial launch state
    fun init() {
        if (!ServiceUtils.isConnected(applicationContext)) {
            thread {
                if (haveInfo()) {
                    // check from local
                    updateFromLocal()
                } else {
                    // no local info warning
                    view.showNoLocalInfo()
                }
            }
        } else {
            // get weather info from api
            if (lastSeeMode == SeeWeatherMode.FROM_CITY_NAME) {
                searchByCity(lastCityName)
            } else {
                searchByCurrentLocation()
            }
        }
    }

    fun searchByCity(cityName: String) {
        if (cityName.isEmpty()) return
        tryLoading(cityName)
    }


    fun searchByCurrentLocation() {
        if (ServiceUtils.locPermission(applicationContext)) {
            ServiceUtils.getLocManager(applicationContext)?.let { locationManager ->
                if (ServiceUtils.isLocEnable(applicationContext)) {
                    getLocation(locationManager)?.let { location ->
                        tryLoading("${location.latitude}", "${location.longitude}")
                    } ?: view.showNoHaveLocation()
                } else {
                    // location is off
                    view.checkEnableLocation()
                }
            } ?: view.checkEnableLocation()
        } else {
            view.checkPermissions()
        }
    }

    // stop running task
    fun stopTask() {
        loadTask?.cancel(true)
        loadTask = null
    }

    // get location
    @SuppressLint("MissingPermission") // have check on top
    private fun getLocation(locationManager: LocationManager): Location? {
        var location: Location?
        // try get from gps
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            60 * 1_000,
            100.toFloat(),
            this
        )
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        // if gps don't have location try get from network
        if (location == null) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                60 * 1_000,
                100.toFloat(),
                this
            )
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        return location
    }

    // get info from DB
    private fun updateFromLocal() {
        db.getLastActual()?.let { m -> view.setInfo(haveError = false, error = null, model = m) }
    }

    // check have info
    private fun haveInfo() = db.count() > 0

    // try loading data from api and save success result to DB
    private fun tryLoading(vararg params: String) {
        if (!ServiceUtils.isConnected(applicationContext)) {
            view.showNoNetworkInfo()
            return
        }
        view.startLoading()
        thread {
            stopTask()
            loadTask = LoadInfoTask(sharedPreferenceHelper)
            val answer = loadTask?.execute(*params)?.get()
            if (answer != null) {
                val model = answer.getBody().toDbModel()
                if (model != null) {
                    db.insert(model)
                }
                view.setInfo(
                    haveError = answer.getError() != null,
                    error = answer.getError(),
                    model = model
                )
            } else {
                view.setInfo(
                    haveError = true,
                    error = ErrorModel(-1001, "Something wrong"),
                    model = null
                )
            }
            stopTask()
        }
    }

    override fun onLocationChanged(p0: Location?) {}
    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
    override fun onProviderEnabled(p0: String?) {}
    override fun onProviderDisabled(p0: String?) {}
}

class LoadInfoTask(private val sharedPreferenceHelper: SharedPreferenceHelper) : AsyncTask<String, Unit, Response<WeatherResponseModel>?>() {

    private val api: WeatherApi = WeatherApi()

    override fun doInBackground(vararg params: String?): Response<WeatherResponseModel>? {
        return when (params.size) {
            1 -> {
                val city = params.component1()
                return city?.let {
                    sharedPreferenceHelper.setSeeMode(SeeWeatherMode.FROM_CITY_NAME)
                    sharedPreferenceHelper.setLastCityName(city)
                    api.getWeatherInfoByCityName(city)
                }
            }
            2 -> {
                val lat = params.component1()?.toDoubleOrNull()
                val lon = params.component2()?.toDoubleOrNull()
                sharedPreferenceHelper.setSeeMode(SeeWeatherMode.FROM_LOCATION)
                return if (lat != null && lon != null) api.getWeatherInfoByLoc(lat, lon)
                else null
            }
            else -> null
        }
    }
}