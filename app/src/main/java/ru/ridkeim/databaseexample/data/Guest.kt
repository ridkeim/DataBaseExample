package ru.ridkeim.databaseexample.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guests")
class Guest(
    @PrimaryKey(autoGenerate = true)
    var guestId : Long = 0L,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "gender", defaultValue = GENDER_UNKNOWN.toString())
    var gender: Int = GENDER_UNKNOWN,
    @ColumnInfo(name = "age", defaultValue = "0")
    var age: Int,
    @ColumnInfo(name = "city")
    var city: String
){
    var ageString : String
        get() = age.toString()
        set(value){
            age = value.toIntOrNull() ?: 0
        }
    companion object {
        const val GENDER_FEMALE = 0
        const val GENDER_MALE = 1
        const val GENDER_UNKNOWN = 2

        fun getInstance() : Guest{

            return Guest(
                name = "",
                city = "",
                age = 0
            )
        }
    }
}