package ru.ridkeim.databaseexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import ru.ridkeim.databaseexample.data.HotelContract
import ru.ridkeim.databaseexample.databinding.ActivityEditorBinding

class EditorActivity : AppCompatActivity() {

    companion object{
        private const val NOT_SAVED_USER_ID = -1L
        const val KEY_GUEST_ID = "guest_id"

    }

    private var guestId = NOT_SAVED_USER_ID
    private lateinit var viewModel: EditorViewModel
    private lateinit var aeBinding: ActivityEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aeBinding =
            DataBindingUtil.setContentView<ActivityEditorBinding>(this, R.layout.activity_editor)
        setSupportActionBar(aeBinding.sharedAppbarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        guestId = intent.getLongExtra(KEY_GUEST_ID, NOT_SAVED_USER_ID)
        val model : EditorViewModel by viewModels {
            EditorViewModel.EditorViewModelFactory(guestId,application)
        }
        viewModel = model
        aeBinding.model = model
        aeBinding.contentEditor.model = model
        aeBinding.lifecycleOwner = this
        model.showMessage.observe(this){
            if(it) {
                Toast.makeText(this,model.message.value,Toast.LENGTH_SHORT).show()
                model.messageShown()
            }
        }
        model.dataStateSaved.observe(this){
            if(it){
                finish()
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
                viewModel.save()
                return true
            }
            R.id.action_delete -> {
                viewModel.remove()
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
        aeBinding.contentEditor.spinnerGender.apply {
            adapter = genderSpinnerAdapter
            setSelection(HotelContract.GuestEntry.GENDER_UNKNOWN)
            isEnabled = false
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.updateGender(position)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    viewModel.updateGender(HotelContract.GuestEntry.GENDER_UNKNOWN)
                }
            }
            viewModel.dataStateLoaded.observe(this@EditorActivity){
                if(it){
                    setSelection(viewModel.guest.value?.gender ?: HotelContract.GuestEntry.GENDER_UNKNOWN)
                    isEnabled = true
                }
            }
        }
    }
}