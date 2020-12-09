package ru.ridkeim.databaseexample

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.ridkeim.databaseexample.adapter.CustomRecyclerAdapter
import ru.ridkeim.databaseexample.data.HotelContract.GuestEntry
import ru.ridkeim.databaseexample.data.HotelDbHelper


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerAdapter: CustomRecyclerAdapter
    private lateinit var dbHelper : SQLiteOpenHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            val editorIntent = Intent(this, EditorActivity::class.java)
            startActivity(editorIntent)
        }
        dbHelper = HotelDbHelper(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = CustomRecyclerAdapter(null)
        recyclerView.adapter = recyclerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_inserts -> {
                insertTestGuest()
                return true
            }
            R.id.action_settings -> {
                val editorIntent = Intent(this, EditorActivity::class.java).apply {
                    action = EditorActivity.ACTION_EDIT_GUEST
                    putExtra(EditorActivity.KEY_GUEST_ID,5L)
                }
                startActivity(editorIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getCursor() : Cursor{
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
                GuestEntry._ID,
                GuestEntry.COLUMN_NAME,
                GuestEntry.COLUMN_CITY,
                GuestEntry.COLUMN_GENDER,
                GuestEntry.COLUMN_AGE
        )
        return db.query(
                GuestEntry.TABLE_NAME,
                projection,
                null, null, null, null, null
        )
    }

    private fun insertTestGuest() {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(GuestEntry.COLUMN_NAME, "Мурзик")
        values.put(GuestEntry.COLUMN_CITY, "Мурманск")
        values.put(GuestEntry.COLUMN_GENDER, GuestEntry.GENDER_MALE)
        values.put(GuestEntry.COLUMN_AGE, 7)
        db.insert(GuestEntry.TABLE_NAME, null, values)
        recyclerAdapter.swapCursor(getCursor())
    }

    override fun onStart() {
        super.onStart()
        recyclerAdapter.swapCursor(getCursor())
    }

    override fun onStop() {
        super.onStop()
        dbHelper.close()
    }

}