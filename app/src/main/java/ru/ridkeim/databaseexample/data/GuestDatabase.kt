package ru.ridkeim.databaseexample.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Guest::class], version = 2,exportSchema = false)
abstract class GuestDatabase : RoomDatabase() {

    abstract val guestDatabaseDao : GuestDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE : GuestDatabase? = null

        fun getInstance(context : Context): GuestDatabase {
            return INSTANCE ?: synchronized(this) {
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        GuestDatabase::class.java,
                        "hotel.db")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                instance
            }
        }
    }

}