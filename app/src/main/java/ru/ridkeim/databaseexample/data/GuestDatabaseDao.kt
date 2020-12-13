package ru.ridkeim.databaseexample.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GuestDatabaseDao {

    @Insert
    suspend fun insert(guest : Guest)

    @Update
    suspend fun update(guest : Guest)

    @Query("select * from guests where guestId = :id")
    suspend fun get(id : Long) : Guest?

    @Query("select * from guests order by guestId desc")
    fun getAllGuests() : LiveData<List<Guest>>
}