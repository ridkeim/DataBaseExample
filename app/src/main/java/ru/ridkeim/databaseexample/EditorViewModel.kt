package ru.ridkeim.databaseexample

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.ridkeim.databaseexample.data.Guest
import ru.ridkeim.databaseexample.data.HotelContract
import ru.ridkeim.databaseexample.data.HotelDbHelper

class EditorViewModel private constructor(private val guestId : Long, private val app : Application) : AndroidViewModel(app) {

    private val tag = EditorViewModel::class.simpleName
    private val dbHelper : HotelDbHelper = HotelDbHelper(app)

    private val _dataStateSaved = MutableLiveData(false)
    val dataStateSaved : LiveData<Boolean>
        get() = _dataStateSaved

    private val _dataStateLoaded = MutableLiveData(false)
    val dataStateLoaded : LiveData<Boolean>
        get() = _dataStateLoaded

    private val _showMessage = MutableLiveData(false)
    val showMessage : LiveData<Boolean>
        get() = _showMessage

    private val _message = MutableLiveData("")
    val message : LiveData<String>
        get() = _message

    val guest = liveData {
        val data = loadGuest(guestId)
        emit(data)
        _dataStateLoaded.postValue(true)
    }

    private suspend fun loadGuest(guestId: Long) : Guest{
        return if(guestId != -1L) {
            if(BuildConfig.DEBUG){
                delay(2000)
            }
            val db = dbHelper.readableDatabase
            val cursor = db.query(
                    HotelContract.GuestEntry.TABLE_NAME,
                    Guest.projection,
                    "${HotelContract.GuestEntry._ID}=?",
                    arrayOf(guestId.toString()),
                    null, null, null
            )
            val guest = Guest.from(cursor)
            Log.d(tag, "guest _dataStatePosted guest=$guest, this=$this")
            guest
        } else{
            Guest(-1L)
        }
    }

    fun save(){
        if(dataStateLoaded.value == false){
            return
        }
        if(-1L == guestId){
            insertGuest()
        }else{
            updateGuest()
        }
    }

    private fun updateGuest() {
        viewModelScope.launch {
            val db = dbHelper.writableDatabase
            val values = guest.value?.let {
                Guest.toContentValues(it)
            }
            val updatedRowsCount = db.update(HotelContract.GuestEntry.TABLE_NAME,
                    values,
                    "${HotelContract.GuestEntry._ID}=?",
                    arrayOf(guestId.toString())
            )
            val message = when(updatedRowsCount){

                1 -> app.getString(R.string.message_record_has_been_updated)
                0 -> app.getString(R.string.message_record_has_not_been_updated)
                else -> app.getString(R.string.message_confused2)
            }
            showMessage(message)
            _dataStateSaved.postValue(1 == updatedRowsCount)
            Log.d(tag, "update _dataStatePosted $this")
        }
    }

    private fun insertGuest() {
        viewModelScope.launch {
            val db = dbHelper.writableDatabase
            val values = guest.value?.let {
                Guest.toContentValues(it)
            }
            val rowId = db.insert(HotelContract.GuestEntry.TABLE_NAME, null, values)
            val message = when(rowId){
                -1L -> app.getString(R.string.message_record_has_not_been_added)
                else -> app.getString(R.string.message_record_has_been_added_with_Id,rowId)
            }
            showMessage(message)
            _dataStateSaved.postValue(-1L != rowId)
            Log.d(tag, "insert _dataStatePosted $this")
        }
    }

    fun remove() {
        if(guestId != -1L){
            viewModelScope.launch {
                val db = dbHelper.writableDatabase
                val deletedRowsCount = db.delete(HotelContract.GuestEntry.TABLE_NAME,
                        "${HotelContract.GuestEntry._ID}=?",
                        arrayOf(guestId.toString())
                )
                val message = when(deletedRowsCount){
                    1 -> app.getString(R.string.message_record_has_been_removed)
                    0 -> app.getString(R.string.message_record_has_not_been_removed)
                    else -> app.getString(R.string.message_confused1)
                }
                showMessage(message)
                _dataStateSaved.postValue(1 == deletedRowsCount)
                Log.d(tag, "remove _dataStatePosted $this")
            }
        }else{
            _dataStateSaved.postValue(true)
        }
    }

    private fun showMessage(msg : String){
        _message.postValue(msg)
        _showMessage.postValue(true)
    }

    fun messageShown(){
        _showMessage.postValue(false)
    }

    fun updateGender(gender : Int){
        if(dataStateLoaded.value == true){
            guest.value?.gender = gender
        }
    }

    override fun onCleared() {
        super.onCleared()
        dbHelper.close()
        Log.d(tag, "onCleared $this")
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