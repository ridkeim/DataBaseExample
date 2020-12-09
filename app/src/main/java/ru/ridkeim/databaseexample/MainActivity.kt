package ru.ridkeim.databaseexample

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.ridkeim.databaseexample.data.HotelContract.GuestEntry
import ru.ridkeim.databaseexample.data.HotelContract.GuestEntry.Companion
import ru.ridkeim.databaseexample.data.HotelDbHelper


class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper : SQLiteOpenHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        Log.d(MainActivity::class.qualifiedName, "toolbar $supportActionBar")
        fab.setOnClickListener { view ->
            val editorIntent = Intent(this, EditorActivity::class.java)
            startActivity(editorIntent)
        }
        dbHelper = HotelDbHelper(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_inserts -> {
                insertGuest()
                displayDatabaseInfo()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        displayDatabaseInfo()
    }
    private fun displayDatabaseInfo(){
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
                Companion._ID,
                Companion.COLUMN_NAME,
                Companion.COLUMN_CITY,
                Companion.COLUMN_GENDER,
                Companion.COLUMN_AGE
        )
        val cursor = db.query(
                Companion.TABLE_NAME,
                projection,
                null, null, null, null, null
        )
        cursor?.use {
            val displayTextView = findViewById<TextView>(R.id.text_view_info)
            displayTextView.text = getString(R.string.db_content,cursor.count)
            displayTextView.append("\n${Companion._ID} - " +
                    "${Companion.COLUMN_NAME} - " +
                    "${Companion.COLUMN_CITY} - " +
                    "${Companion.COLUMN_GENDER} - " +
                    "${Companion.COLUMN_AGE}\n"
            )
            val columnIndexId = it.getColumnIndex(Companion._ID)
            val columnIndexName = it.getColumnIndex(Companion.COLUMN_NAME)
            val columnIndexCity = it.getColumnIndex(Companion.COLUMN_CITY)
            val columnIndexGender = it.getColumnIndex(Companion.COLUMN_GENDER)
            val columnIndexAge = it.getColumnIndex(Companion.COLUMN_AGE)

            while (it.moveToNext()) {
                val id = it.getInt(columnIndexId)
                val name = it.getString(columnIndexName)
                val city = it.getString(columnIndexCity)
                val gender = it.getInt(columnIndexGender)
                val age = it.getInt(columnIndexAge)
                displayTextView.append("\n$id - $name - $city - $gender - $age")
            }
        }
    }

    private fun insertGuest() {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(GuestEntry.COLUMN_NAME, "Мурзик")
        values.put(GuestEntry.COLUMN_CITY, "Мурманск")
        values.put(GuestEntry.COLUMN_GENDER, GuestEntry.GENDER_MALE)
        values.put(GuestEntry.COLUMN_AGE, 7)
        db.insert(GuestEntry.TABLE_NAME, null, values)
    }

}