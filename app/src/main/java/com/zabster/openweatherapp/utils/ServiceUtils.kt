package com.zabster.openweatherapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import androidx.core.content.ContextCompat

object ServiceUtils {

    fun getLocPermissions() =
        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

    fun locPermission(context: Context): Boolean = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    fun isLocEnable(context: Context): Boolean =
        getLocManager(context)?.let { lm ->
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        } ?: false

    fun getLocManager(context: Context) =
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    fun isConnected(context: Context): Boolean {
        val connect = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            !connect?.allNetworks.isNullOrEmpty()
        } else connect?.activeNetworkInfo?.isConnected == true
    }
}