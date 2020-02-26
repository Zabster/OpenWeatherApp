package com.zabster.openweatherapp.app

import android.app.Application
import android.content.Context
import com.zabster.openweatherapp.db.AppDataBase

class App: Application() {

    companion object {
        lateinit var appDataBase: AppDataBase
            private set
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appDataBase = AppDataBase(this)
        appContext = this
    }

    override fun onTerminate() {
        appDataBase.close()
        super.onTerminate()
    }
}