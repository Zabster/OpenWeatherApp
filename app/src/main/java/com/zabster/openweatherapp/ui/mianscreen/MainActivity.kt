package com.zabster.openweatherapp.ui.mianscreen

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.zabster.openweatherapp.R
import com.zabster.openweatherapp.db.models.WeatherDbModel
import com.zabster.openweatherapp.enums.SeeWeatherMode
import com.zabster.openweatherapp.net.models.errors.ErrorModel
import com.zabster.openweatherapp.utils.*
import kotlinx.android.synthetic.main.activity_main.*

private const val LOC_PERM_REQUEST_CODE = 1012

class MainActivity : AppCompatActivity(), IMain {

    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = MainPresenter(this)
        presenter.init()
        initListeners()
        initBaseData()
    }

    private fun initListeners() {
        updateBtn.setOnClickListener {
            updateBtn.requestFocus()
            val id = if (radioGroup.checkedRadioButtonId == R.id.btnFromCity) 0 else 1
            when (SeeWeatherMode.getMode(id)) {
                SeeWeatherMode.FROM_CITY_NAME -> {
                    val cityName = cityNameEdit.text.toString()
                    if (cityName.trim().isNotEmpty()) {
                        presenter.searchByCity(cityName)
                    } else {
                        showEmptyCityNameError()
                    }
                }
                SeeWeatherMode.FROM_LOCATION -> {
                    presenter.searchByCurrentLocation()
                }
            }
        }
        radioGroup.setOnCheckedChangeListener { _, i ->
            cityNameEdit.setInvisible(i == R.id.btnFromCity)
        }
    }

    // set navigate menu
    private fun initBaseData() {
        // check last know mode
        val lastMode = presenter.lastSeeMode
        val id = if (lastMode == SeeWeatherMode.FROM_CITY_NAME) R.id.btnFromCity
        else R.id.btnFromLoc
        radioGroup.check(id)
        if (lastMode == SeeWeatherMode.FROM_CITY_NAME) {
            cityNameEdit.setText(presenter.lastCityName)
        }
    }

    // set data from network or local
    private fun setData(model: WeatherDbModel) {
        
        tempText.text = getString(R.string.temp_text, model.temp.toInt().toString())
        descText.text = getString(R.string.desc_text, model.title, model.description)

        humidityText.text = getString(R.string.humidity_text, model.humidity.toString())
        pressureText.text = getString(R.string.pressure_text, model.pressure.toString())

        sunriseDateText.text =
            getString(R.string.sunrise_text, Formatter.simpleDateFormat(model.sunrise))
        sunsetDateText.text =
            getString(R.string.sunset_text, Formatter.simpleDateFormat(model.sunset))

        windSpeedText.text = getString(R.string.wind_speed_text, model.windSpeed.toString())
        windDegText.text = getString(R.string.wind_deg_text, model.windDeg.toString())
    }

    private fun hideAll() {
        progressBar?.invisible()
        mainInfoContainer?.invisible()
    }

    override fun checkPermissions() {
        runOnUiThread {
            ActivityCompat
                .requestPermissions(this, presenter.locPermission, LOC_PERM_REQUEST_CODE)
        }
    }

    override fun checkEnableLocation() {
        runOnUiThread {
            val settingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingIntent)
        }
    }

    override fun startLoading() {
        runOnUiThread {
            progressBar?.visible()
            mainInfoContainer?.invisible()
        }
    }

    override fun stopLoading() {
        runOnUiThread {
            progressBar?.invisible()
            mainInfoContainer?.visible()
            hideKeyBoard()
        }
    }

    override fun setInfo(haveError: Boolean, error: ErrorModel?, model: WeatherDbModel?) {
        runOnUiThread {
            if (haveError) error?.message?.let { msg ->
                hideAll()
                toast(msg)
            } ?: showEmptyError()
            else model?.let { m ->
                setData(m)
                stopLoading()
            } ?: showEmptyError()
        }
    }

    override fun showNoNetworkInfo() {
        runOnUiThread {
            toast(R.string.no_network_error)
        }
    }

    override fun showNoLocalInfo() {
        runOnUiThread {
            toast(R.string.no_local_error)
        }
    }

    override fun showNoHaveLocation() {
        runOnUiThread {
            toast(R.string.no_location_error)
        }
    }

    private fun showEmptyError() {
        runOnUiThread {
            hideAll()
            toast(R.string.empty_other_error)
        }
    }

    override fun showEmptyCityNameError() {
        runOnUiThread {
            toast(R.string.no_city_name_error)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == LOC_PERM_REQUEST_CODE) {
            presenter.searchByCurrentLocation()
        }
    }

    override fun onDestroy() {
        presenter.stopTask()
        super.onDestroy()
    }
}
