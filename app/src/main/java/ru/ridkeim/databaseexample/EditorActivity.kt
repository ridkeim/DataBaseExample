package ru.ridkeim.databaseexample

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.content_editor.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.ridkeim.databaseexample.data.HotelContract
import ru.ridkeim.databaseexample.data.HotelDbHelper
import java.lang.IllegalArgumentException

class EditorActivity : AppCompatActivity() {

    companion object{
        private const val NOT_SAVED_USER_ID = -1L
        const val ACTION_NEW_GUEST = "ru.ridkeim.databaseexample.action.NEW_GUEST"
        const val ACTION_EDIT_GUEST = "ru.ridkeim.databaseexample.action.EDIT_GUEST"
        const val KEY_GUEST_ID = "guest_id"

    }

    private var mGender = 2
    private var guestId = -1L
    private val hotelDbHelper = HotelDbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        setSupportActionBar(toolbar)
        when(intent.action){
            ACTION_EDIT_GUEST -> {
                guestId = intent.getLongExtra(KEY_GUEST_ID, NOT_SAVED_USER_ID)
                if(NOT_SAVED_USER_ID == guestId){
                    finish()
                }
                if(!loadGuest()){
                    finish()
                }
            }
        }
        setupSpinner()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.editor_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_save -> {
                if (saveGuest()){
                    finish()
                }
                return true
            }
            R.id.action_delete -> {
                if(removeGuest()){
                    finish()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupSpinner() {
        val genderSpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.array_gender_options,
            android.R.layout.simple_spinner_item
        )
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinner_gender.adapter = genderSpinnerAdapter
        spinner_gender.setSelection(mGender)
        spinner_gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mGender = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                mGender = 2
            }

        }
    }

    private fun loadGuest() : Boolean{
        val db = hotelDbHelper.readableDatabase
        val projection = arrayOf(
                HotelContract.GuestEntry._ID,
                HotelContract.GuestEntry.COLUMN_NAME,
                HotelContract.GuestEntry.COLUMN_CITY,
                HotelContract.GuestEntry.COLUMN_GENDER,
                HotelContract.GuestEntry.COLUMN_AGE
        )
        val cursor = db.query(
                HotelContract.GuestEntry.TABLE_NAME,
                projection,
                "${HotelContract.GuestEntry._ID}=?",
                arrayOf(guestId.toString()),
                null,null,null
        )
        if(cursor.count == 1){
            cursor?.use {
                val columnIndexName = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_NAME)
                val columnIndexCity = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_CITY)
                val columnIndexGender = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_GENDER)
                val columnIndexAge = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_AGE)
                if(it.moveToFirst()){
                    edit_guest_name.setText(it.getString(columnIndexName))
                    edit_guest_city.setText(it.getString(columnIndexCity))
                    mGender = it.getInt(columnIndexGender)
                    edit_guest_age.setText(it.getString(columnIndexAge))
                }
            }
            return true
        }else{
            Toast.makeText(this,"Записи нет или их слишком много",Toast.LENGTH_SHORT).show()
            return false
        }
    }

    private fun saveGuest() : Boolean {
        return if(NOT_SAVED_USER_ID == guestId){
            insertGuest()
        }else{
            updateGuest()
        }
    }

    private fun updateGuest() : Boolean {
        val db = hotelDbHelper.writableDatabase
        val values = try {
            getContentValues()
        } catch (e : IllegalArgumentException){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            return false
        }
        val updatedRowsCount = db.update(HotelContract.GuestEntry.TABLE_NAME,
                values,
                "${HotelContract.GuestEntry._ID}=?",
                arrayOf(guestId.toString())
        )
        val message = when (updatedRowsCount) {
            1 -> "Обновлена 1 запись"
            0 -> "Запись не обновлена"
            else ->"Oh, is it real? Oo"
        }
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        return 1 == updatedRowsCount
    }

    private fun insertGuest() : Boolean {
        val db = hotelDbHelper.writableDatabase
        val values = try {
            getContentValues()
        } catch (e : IllegalArgumentException){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            return false
        }
        val rowId = db.insert(HotelContract.GuestEntry.TABLE_NAME, null, values)
        val result = (rowId == -1L)
        val message = if (result) {
            "Ошибка при заведении гостя"
        }else{
            "Гость заведен под номером $rowId"
        }
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        return result
    }

    private fun getContentValues(): ContentValues {
        val name = edit_guest_name.text.toString().trim()
        val city = edit_guest_city.text.toString().trim()
        val age = edit_guest_age.text.toString().trim()
        if(name.isEmpty()){
            throw IllegalArgumentException("Поле \"имя\" не может быть пустым")
        }
        if(city.isEmpty()){
            throw IllegalArgumentException("Поле \"город\" не может быть пустым")
        }
        val values = ContentValues()
        values.put(HotelContract.GuestEntry.COLUMN_NAME, name)
        values.put(HotelContract.GuestEntry.COLUMN_CITY, city)
        values.put(HotelContract.GuestEntry.COLUMN_GENDER, mGender)
        values.put(HotelContract.GuestEntry.COLUMN_AGE, age)
        return values
    }

    private fun removeGuest() : Boolean{
        if(NOT_SAVED_USER_ID != guestId){
            val db = hotelDbHelper.writableDatabase
            val deletedRowsCount = db.delete(HotelContract.GuestEntry.TABLE_NAME,
                    "${HotelContract.GuestEntry._ID}=?",
                    arrayOf(guestId.toString())
            )
            val message = when (deletedRowsCount) {
                1 -> "Удалена 1 запись"
                0 -> "Запись не удалена"
                else -> "Oh, is it real? Oo"
            }
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
            return 1 == deletedRowsCount
        }
        return false
    }
}