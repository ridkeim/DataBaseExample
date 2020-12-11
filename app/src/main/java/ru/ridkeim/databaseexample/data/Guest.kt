package ru.ridkeim.databaseexample.data

import android.content.ContentValues
import android.database.Cursor

class Guest(var id : Long) {
    var age : String = ""
    var city : String = ""
    var name : String = ""
    var gender : Int = HotelContract.GuestEntry.GENDER_UNKNOWN

    companion object {

        val projection = arrayOf(
            HotelContract.GuestEntry._ID,
            HotelContract.GuestEntry.COLUMN_NAME,
            HotelContract.GuestEntry.COLUMN_CITY,
            HotelContract.GuestEntry.COLUMN_GENDER,
            HotelContract.GuestEntry.COLUMN_AGE
        )

        fun from(cursor : Cursor) : Guest{
            if(cursor.count == 1){
                cursor.use {
                    val columnIndexId = it.getColumnIndex(HotelContract.GuestEntry._ID)
                    val columnIndexName = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_NAME)
                    val columnIndexCity = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_CITY)
                    val columnIndexGender = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_GENDER)
                    val columnIndexAge = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_AGE)
                    if(it.moveToFirst()){
                        return Guest(it.getLong(columnIndexId)).apply {
                            name = it.getString(columnIndexName)
                            city = it.getString(columnIndexCity)
                            gender = it.getInt(columnIndexGender)
                            age = it.getInt(columnIndexAge).toString()
                        }
                    }
                }
            }
            return Guest(-1L)
        }

        fun toContentValues(value: Guest): ContentValues {
            val values = ContentValues()
            values.put(HotelContract.GuestEntry.COLUMN_NAME, value.name)
            values.put(HotelContract.GuestEntry.COLUMN_CITY, value.city)
            values.put(HotelContract.GuestEntry.COLUMN_GENDER, value.gender)
            values.put(HotelContract.GuestEntry.COLUMN_AGE, value.age)
            return values
        }

        fun loadAllFrom(cursor : Cursor?) : List<Guest>{
            val list = mutableListOf<Guest>()
            cursor?.use {
                val columnIndexId = it.getColumnIndex(HotelContract.GuestEntry._ID)
                val columnIndexName = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_NAME)
                val columnIndexCity = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_CITY)
                val columnIndexGender = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_GENDER)
                val columnIndexAge = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_AGE)
                while (cursor.moveToNext()){
                    list.add(
                        Guest(it.getLong(columnIndexId)).apply {
                            name = it.getString(columnIndexName)
                            city = it.getString(columnIndexCity)
                            gender = it.getInt(columnIndexGender)
                            age = it.getInt(columnIndexAge).toString()
                        }
                    )
                }
            }
            return list
        }


    }
}