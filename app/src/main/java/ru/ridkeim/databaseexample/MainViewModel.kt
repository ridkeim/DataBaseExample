package ru.ridkeim.databaseexample

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.ridkeim.databaseexample.data.Guest
import ru.ridkeim.databaseexample.data.GuestDatabaseDao
import ru.ridkeim.databaseexample.data.HotelContract
import ru.ridkeim.databaseexample.data.HotelDbHelper

class MainViewModel(private val database: GuestDatabaseDao, app : Application) : AndroidViewModel(app){
    private val tag = MainViewModel::class.simpleName
    val list : LiveData<List<Guest>> = database.getAllGuests()

    fun insertTestGuest() {
        val guest = Guest(
            name = "Мурзик",
            gender = HotelContract.GuestEntry.GENDER_MALE,
            age = 7,
            city = "Мурмянск"
        )
        viewModelScope.launch {
            database.insert(guest)
        }
    }

    class MainViewModelFactory(
        private val dataSource : GuestDatabaseDao,
        private val app : Application) : ViewModelProvider.Factory{
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)){
                return MainViewModel(dataSource,app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}