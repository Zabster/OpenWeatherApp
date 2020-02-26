package com.zabster.openweatherapp.db.actions


import android.database.DatabaseUtils
import android.provider.BaseColumns
import android.util.Log
import com.zabster.openweatherapp.db.AppDataBase
import com.zabster.openweatherapp.db.models.WeatherDbModel
import com.zabster.openweatherapp.db.models.WeatherEntry
import com.zabster.openweatherapp.db.models.toContentValues
import com.zabster.openweatherapp.db.models.toDBModel
import java.lang.Exception
import java.util.*

// use another thread
private const val WeatherActions_TAG = "WeatherActions"

class WeatherActions(private val db: AppDataBase) {

    fun insert(model: WeatherDbModel) {
        try {
            db.writableDatabase.insertOrThrow(
                WeatherEntry.TABLE_NAME,
                null,
                model.toContentValues()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // todo add error
        }
    }

    fun upgrade(model: WeatherDbModel) {
        try {
            db.writableDatabase.update(
                WeatherEntry.TABLE_NAME,
                model.toContentValues(),
                "${BaseColumns._ID} LIKE ?",
                arrayOf(model.id.toString())
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // todo add error
        }
    }

    fun getLastActual(): WeatherDbModel? {
        try {
            val selection = "${WeatherEntry.COLUMN_INSERT_DATE} < ?"
            val selectionArgs = arrayOf("${Date().time}")
            val order = "${WeatherEntry.COLUMN_INSERT_DATE} DESC"
            val limit = "1"
            val cursor = db.readableDatabase.query(
                WeatherEntry.TABLE_NAME,
                WeatherEntry.COLUMN_ALL,
                selection,
                selectionArgs,
                null,
                null,
                order,
                limit
            )

            var data: WeatherDbModel? = null
            with(cursor) {
                if (moveToNext()) data = toDBModel()
                else Log.i(WeatherActions_TAG, "cursor is empty")
                close()
            }
            return data
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun count(): Long {
        return try {
            return DatabaseUtils.queryNumEntries(db.readableDatabase, WeatherEntry.TABLE_NAME)
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    fun delete(id: Int) {

    }

}