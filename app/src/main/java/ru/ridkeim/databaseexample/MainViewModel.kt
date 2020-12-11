package ru.ridkeim.databaseexample

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.ridkeim.databaseexample.data.Guest
import ru.ridkeim.databaseexample.data.HotelContract
import ru.ridkeim.databaseexample.data.HotelDbHelper

class MainViewModel(app : Application) : AndroidViewModel(app), LifecycleObserver {
    private val tag = MainViewModel::class.simpleName
    private val dbHelper : HotelDbHelper = HotelDbHelper(app)
    private val _list = MutableLiveData<List<Guest>>()
    val list : LiveData<List<Guest>>
        get() =_list

    private fun loadAllGuest() : List<Guest>{
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            HotelContract.GuestEntry.TABLE_NAME,
            Guest.projection,
            null,null,
            null, null, null
        )
        val guests = Guest.loadAllFrom(cursor)
        Log.d(tag, "guests loaded guest=$guests, this=$this")
        return guests
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun loadData(){
        viewModelScope.launch {
            val loadedGuests = loadAllGuest()
            _list.postValue(loadedGuests)
        }
    }

    fun insertTestGuest() {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(HotelContract.GuestEntry.COLUMN_NAME, "Мурзик")
        values.put(HotelContract.GuestEntry.COLUMN_CITY, "Мурманск")
        values.put(HotelContract.GuestEntry.COLUMN_GENDER, HotelContract.GuestEntry.GENDER_MALE)
        values.put(HotelContract.GuestEntry.COLUMN_AGE, 7)
        db.insert(HotelContract.GuestEntry.TABLE_NAME, null, values)
        loadData()
    }

    override fun onCleared() {
        dbHelper.close()
        super.onCleared()
    }

    class MainViewModelFactory(val app : Application) : ViewModelProvider.AndroidViewModelFactory(app){
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)){
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}