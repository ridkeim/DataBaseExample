package ru.ridkeim.databaseexample.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class HotelDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION) {
    companion object {
        val LOG_TAG = HotelDbHelper::class.qualifiedName
        const val DATABASE_NAME = "hotel.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sqlCreateGuestTable = "CREATE TABLE ${HotelContract.GuestEntry.TABLE_NAME} (" +
                "${HotelContract.GuestEntry._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${HotelContract.GuestEntry.COLUMN_NAME} TEXT NOT NULL, " +
                "${HotelContract.GuestEntry.COLUMN_CITY} TEXT NOT NULL, " +
                "${HotelContract.GuestEntry.COLUMN_GENDER} INTEGER NOT NULL DEFAULT 3, " +
                "${HotelContract.GuestEntry.COLUMN_AGE} INTEGER NOT NULL DEFAULT 0);"
        db!!.execSQL(sqlCreateGuestTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.w(LOG_TAG, "Обновляемся с версии $oldVersion на версию $newVersion")
        db!!.execSQL("DROP TABLE IF EXISTS ${HotelContract.GuestEntry.TABLE_NAME}")
        onCreate(db)
    }
}