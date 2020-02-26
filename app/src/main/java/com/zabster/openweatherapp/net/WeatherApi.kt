package com.zabster.openweatherapp.net

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.zabster.openweatherapp.net.models.errors.ErrorModel
import com.zabster.openweatherapp.net.models.response.Response
import com.zabster.openweatherapp.net.models.response.WeatherResponseModel
import java.lang.Exception
import java.net.URL
import javax.net.ssl.HttpsURLConnection

const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
const val appId = "ecf0ae2992ad455ea99248cf8efcaa7e"

private const val WeatherApi_TAG = "WeatherApi"

class WeatherApi {

    private var connection: HttpsURLConnection? = null

    private val gson: Gson = GsonBuilder()
        .apply {
            serializeNulls()
        }.create()

    fun getWeatherInfoByLoc(lat: Double, lon: Double): Response<WeatherResponseModel>? {
        val url = URL("$BASE_URL?lat=$lat&lon=$lon&units=metric&appid=$appId")
        return handle(url)
    }

    fun getWeatherInfoByCityName(cityName: String): Response<WeatherResponseModel>? {
        val url = URL("$BASE_URL?q=$cityName&units=metric&appid=$appId")
        return handle(url)
    }

    private fun connect(url: URL) {
        connection = url.openConnection() as? HttpsURLConnection
    }

    private fun disconnect() {
        try {
            connection?.inputStream?.close()
        } catch (e: Exception) {

        }
        connection?.disconnect()
        connection = null
    }

    private fun handle(url: URL): Response<WeatherResponseModel>? {
        return try {
            connect(url)
            connection?.run {
                // set timeouts
                readTimeout = 3_000
                connectTimeout = 3_000

                requestMethod = "GET"

                when (responseCode) {
                    HttpsURLConnection.HTTP_OK -> {
                        inputStream?.let { inputStream ->
                            val json = String(inputStream.readBytes())
                            val answerWeather = try {
                                gson.fromJson(json, WeatherResponseModel::class.java)
                            } catch (e: Exception) {
                                null
                            }
                            val errorAnswer = try {
                                gson.fromJson(json, ErrorModel::class.java)
                            } catch (e: Exception) {
                                null
                            }
                            object : Response<WeatherResponseModel> {
                                override fun getBody(): WeatherResponseModel? = answerWeather
                                override fun getError(): ErrorModel? =
                                    if (answerWeather == null) errorAnswer else null
                            }
                        }
                    }
                    else -> {
                        Log.e(
                            WeatherApi_TAG,
                            "fail fetch result, e: $responseCode - $responseMessage"
                        )
                        object : Response<WeatherResponseModel> {
                            override fun getBody(): WeatherResponseModel? = null
                            override fun getError(): ErrorModel? =
                                ErrorModel(responseCode, responseMessage)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(WeatherApi_TAG, "Exception result", e)
            object : Response<WeatherResponseModel> {
                override fun getBody(): WeatherResponseModel? = null
                override fun getError(): ErrorModel? =
                    ErrorModel(-101, e.message)
            }
        } finally {
            disconnect()
        }
    }
}