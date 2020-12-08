package ru.ridkeim.databaseexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.content_editor.*

class EditorActivity : AppCompatActivity() {
    private var mGender = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        val supportActionBar = supportActionBar
        Log.d(EditorActivity::class.qualifiedName,"toolbar $supportActionBar")
        setupSpinner()
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
}