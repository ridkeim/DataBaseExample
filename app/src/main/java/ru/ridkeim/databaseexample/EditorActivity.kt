package ru.ridkeim.databaseexample

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.content_editor.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.ridkeim.databaseexample.data.HotelContract
import ru.ridkeim.databaseexample.data.HotelDbHelper

class EditorActivity : AppCompatActivity() {

    companion object{
        const val NOT_SAVED_USER_ID = -1
    }
    private var mGender = 2
    private var guestId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        setSupportActionBar(toolbar)
        setupSpinner()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.editor_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_save -> {
                saveGuest()
                finish()
                return true
            }
            R.id.action_delete -> {
                removeGuest()
                finish()
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

    private fun saveGuest(){
        if(NOT_SAVED_USER_ID == guestId){
            insertGuest()
        }else{
            updateGuest()
        }
    }

    private fun updateGuest() {

    }

    private fun insertGuest(){
        val name = edit_guest_name.text.toString().trim()
        val city = edit_guest_city.text.toString().trim()
        val age = edit_guest_age.text.toString().trim().toInt()
        val db = HotelDbHelper(this).writableDatabase
        val values = ContentValues()
        values.put(HotelContract.GuestEntry.COLUMN_NAME,name)
        values.put(HotelContract.GuestEntry.COLUMN_CITY,city)
        values.put(HotelContract.GuestEntry.COLUMN_GENDER,mGender)
        values.put(HotelContract.GuestEntry.COLUMN_AGE,age)
        val rowId = db.insert(HotelContract.GuestEntry.TABLE_NAME, null, values)
        val message = if (rowId == -1L) {
            "Ошибка при заведении гостя"
        }else{
            "Гость заведен под номером $rowId"
        }
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    private fun removeGuest(){

    }
}