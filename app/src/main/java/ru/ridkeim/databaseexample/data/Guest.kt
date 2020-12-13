package ru.ridkeim.databaseexample.data

import android.content.ContentValues
import android.database.Cursor
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guests")
class Guest(
    @PrimaryKey(autoGenerate = true)
    var guestId : Long = 0L,
    @ColumnInfo(name = "name")
    var name : String = "",
    @ColumnInfo(name = "gender",defaultValue = HotelContract.GuestEntry.GENDER_UNKNOWN.toString())
    var gender : Int = HotelContract.GuestEntry.GENDER_UNKNOWN,
    @ColumnInfo(name = "age", defaultValue = "0")
    var age : Int = 0,
    @ColumnInfo(name = "city")
    var city : String = ""
)