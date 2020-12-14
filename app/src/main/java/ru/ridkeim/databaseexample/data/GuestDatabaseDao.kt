package ru.ridkeim.databaseexample.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GuestDatabaseDao {

    @Insert
    suspend fun insert(guest : Guest) : Long

    @Update
    suspend fun update(guest : Guest) : Int

    @Delete
    suspend fun delete(guest: Guest) : Int

    @Query("select * from guests where guestId = :id")
    suspend fun get(id : Long) : Guest?

    @Query("select * from guests order by guestId desc")
    fun getAllGuests() : LiveData<List<Guest>>
}