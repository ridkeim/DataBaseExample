package ru.ridkeim.databaseexample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        Log.d(MainActivity::class.qualifiedName,"toolbar $supportActionBar")
        fab.setOnClickListener { view ->
            val editorIntent = Intent(this, EditorActivity::class.java)
            startActivity(editorIntent)
        }
    }
}