package ru.ridkeim.databaseexample

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.ridkeim.databaseexample.data.Guest
import ru.ridkeim.databaseexample.data.GuestDatabaseDao
import ru.ridkeim.databaseexample.data.HotelContract

class EditorViewModel private constructor(
    private val guestId : Long,
    private val database : GuestDatabaseDao,
    app : Application) : AndroidViewModel(app) {

    private val tag = EditorViewModel::class.simpleName
    private val isNewUser = (-1L == guestId)
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
        val data = database.get(guestId)
        emit(data ?: Guest())
        _dataStateLoaded.postValue(true)
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
            guest.value?.let {
                database.update(it)
                _dataStateSaved.postValue(true)
            }
//            val message = when(updatedRowsCount){
//                1 -> app.getString(R.string.message_record_has_been_updated)
//                0 -> app.getString(R.string.message_record_has_not_been_updated)
//                else -> app.getString(R.string.message_confused2)
//            }
//            showMessage(message)
            Log.d(tag, "update _dataStatePosted $this")
        }
    }

    private fun insertGuest() {
        viewModelScope.launch {
            guest.value?.let {
                database.insert(it)
                _dataStateSaved.postValue(true)
            }
//            val rowId = 1L
//            val message = when(rowId){
//                -1L -> app.getString(R.string.message_record_has_not_been_added)
//                else -> app.getString(R.string.message_record_has_been_added_with_Id,rowId)
//            }
//            showMessage(message)
            Log.d(tag, "insert _dataStatePosted $this")
        }
    }

    fun remove() {
        if(isNewUser){
            _dataStateSaved.postValue(true)
        }else{
            viewModelScope.launch {
//                val deletedRowsCount = 1
//                val message = when(deletedRowsCount){
//                    1 -> app.getString(R.string.message_record_has_been_removed)
//                    0 -> app.getString(R.string.message_record_has_not_been_removed)
//                    else -> app.getString(R.string.message_confused1)
//                }
//                showMessage(message)
                _dataStateSaved.postValue(true)
                Log.d(tag, "remove _dataStatePosted $this")
            }
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
        Log.d(tag, "onCleared $this")
    }

    class EditorViewModelFactory(
        private val guestId : Long,
        private val dataSource : GuestDatabaseDao,
        val app : Application) : ViewModelProvider.Factory{
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(EditorViewModel::class.java)){
                return EditorViewModel(guestId,dataSource,app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}