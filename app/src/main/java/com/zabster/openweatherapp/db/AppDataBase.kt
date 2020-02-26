package com.zabster.openweatherapp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.zabster.openweatherapp.db.MetaDB.DB_NAME
import com.zabster.openweatherapp.db.MetaDB.DB_VERSION
import com.zabster.openweatherapp.db.models.WeatherEntry

class AppDataBase(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS ${WeatherEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "${WeatherEntry.COLUMN_TITLE} TEXT NOT NULL," +
                "${WeatherEntry.COLUMN_DESC} TEXT NOT NULL," +
                "${WeatherEntry.COLUMN_INSERT_DATE} TEXT NOT NULL," +
                "${WeatherEntry.COLUMN_TEMPERATURE} REAL NOT NULL," +
                "${WeatherEntry.COLUMN_PRESSURE} INTEGER NOT NULL," +
                "${WeatherEntry.COLUMN_HUMIDITY} INTEGER NOT NULL," +
                "${WeatherEntry.COLUMN_WIND_DEG} REAL NOT NULL," +
                "${WeatherEntry.COLUMN_WIND_SPEED} REAL NOT NULL," +
                "${WeatherEntry.COLUMN_SUNSET} INTEGER," +
                "${WeatherEntry.COLUMN_SUNRISE} INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // migrations
    }
}

object MetaDB {
    const val DB_NAME = "Weather_DB"
    const val DB_VERSION = 1
}