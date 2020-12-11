package ru.ridkeim.databaseexample

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import ru.ridkeim.databaseexample.adapter.CustomRecyclerAdapter
import ru.ridkeim.databaseexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){

    private val amBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this,R.layout.activity_main)
    }
    private val viewModel : MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory(application)
    }

    private lateinit var recyclerAdapter: CustomRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        amBinding.lifecycleOwner = this
        setSupportActionBar(amBinding.sharedAppbarLayout.toolbar)
        amBinding.fab.setOnClickListener { view ->
            val editorIntent = Intent(this, EditorActivity::class.java)
            startActivity(editorIntent)
        }
        recyclerAdapter = CustomRecyclerAdapter()
        amBinding.contentMain.recyclerView.also {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = recyclerAdapter
        }
        viewModel.list.observe(this){
            recyclerAdapter.submitList(it)
        }
        lifecycle.addObserver(viewModel)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_insert_test_record -> {
                viewModel.insertTestGuest()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}