package ru.ridkeim.databaseexample

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.ridkeim.databaseexample.data.Guest
import ru.ridkeim.databaseexample.data.GuestDatabaseDao

class MainViewModel(private val database: GuestDatabaseDao, app : Application) : AndroidViewModel(app){

    val list : LiveData<List<Guest>> = database.getAllGuests()

    fun insertTestGuest() {
        val guest = Guest(
            name = "Мурзик",
            gender = Guest.GENDER_MALE,
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