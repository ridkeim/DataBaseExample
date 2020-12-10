package ru.ridkeim.databaseexample

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import ru.ridkeim.databaseexample.data.Guest
import ru.ridkeim.databaseexample.data.HotelContract
import ru.ridkeim.databaseexample.data.HotelDbHelper
import java.lang.IllegalArgumentException

class EditorViewModel private constructor(private val guestId : Long, app : Application) : AndroidViewModel(app) {
    private val tag = EditorViewModel::class.qualifiedName
    private val dbHelper : HotelDbHelper = HotelDbHelper(app)
    private val _dataStateSaved = MutableLiveData(false)
    val dataStateSaved : LiveData<Boolean>
        get() = _dataStateSaved
    private val _guest : MutableLiveData<Guest> by lazy {
        MutableLiveData(Guest(-1L)).also{
            loadGuest(it,guestId)
        }
    }
    val guest : LiveData<Guest>
        get() = _guest

    private fun loadGuest(liveData: MutableLiveData<Guest>,guestId: Long){
        if(guestId != -1L) {
            viewModelScope.run {
                val db = dbHelper.readableDatabase
                val cursor = db.query(
                    HotelContract.GuestEntry.TABLE_NAME,
                    Guest.projection,
                    "${HotelContract.GuestEntry._ID}=?",
                    arrayOf(guestId.toString()),
                    null, null, null
                )
                liveData.value = Guest.from(cursor)
            }
        }
    }

    fun save(){
        if(-1L == guestId){
            insertGuest()
        }else{
            updateGuest()
        }
    }

    private fun updateGuest() {
        viewModelScope.run {
            val db = dbHelper.writableDatabase
            val values = _guest.value?.let {
                Guest.toContentValues(it)
            } ?: return@run
            val updatedRowsCount = db.update(HotelContract.GuestEntry.TABLE_NAME,
                    values,
                    "${HotelContract.GuestEntry._ID}=?",
                    arrayOf(guestId.toString())
            )
            _dataStateSaved.value = 1 == updatedRowsCount
        }
    }

    private fun insertGuest() {
        viewModelScope.run {
            val db = dbHelper.writableDatabase
            val values = _guest.value?.let {
                Guest.toContentValues(it)
            } ?: return@run
            val rowId = db.insert(HotelContract.GuestEntry.TABLE_NAME, null, values)
            _dataStateSaved.value = (-1L != rowId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        dbHelper.close()
        Log.d(tag, "onCleared $this")
    }

    fun remove() {
        if(guestId != -1L){
            viewModelScope.run {
                val db = dbHelper.writableDatabase
                val deletedRowsCount = db.delete(HotelContract.GuestEntry.TABLE_NAME,
                        "${HotelContract.GuestEntry._ID}=?",
                        arrayOf(guestId.toString())
                )
                _dataStateSaved.value = (1 == deletedRowsCount)
            }
        }
    }

    class EditorViewModelFactory(private val guestId : Long, val app : Application) : ViewModelProvider.AndroidViewModelFactory(app){
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(EditorViewModel::class.java)){
                return EditorViewModel(guestId,app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}