package ru.ridkeim.databaseexample.adapter

import android.content.Intent
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*
import ru.ridkeim.databaseexample.EditorActivity
import ru.ridkeim.databaseexample.R
import ru.ridkeim.databaseexample.data.HotelContract

class CustomRecyclerAdapter(var cursor : Cursor?) : RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor?.moveToPosition(position)
        return holder.bind(cursor)
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    class ViewHolder private constructor(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bind(cursor : Cursor?){
            cursor?.let {
                val columnIndexId = it.getColumnIndex(HotelContract.GuestEntry._ID)
                val columnIndexName = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_NAME)
                val columnIndexCity = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_CITY)
                val columnIndexGender = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_GENDER)
                val columnIndexAge = it.getColumnIndex(HotelContract.GuestEntry.COLUMN_AGE)
                val resources = itemView.resources
                itemView.item_name.text = resources.getString(R.string.item_name,
                        it.getString(columnIndexName))
                itemView.item_city.text = resources.getString(R.string.item_city,
                        it.getString(columnIndexCity))
                itemView.item_age.text = resources.getString(R.string.item_age,
                        it.getInt(columnIndexAge).toString())
                itemView.tag = it.getLong(columnIndexId)
                val gender = when(it.getInt(columnIndexGender)){
                    HotelContract.GuestEntry.GENDER_MALE -> resources.getString(R.string.gender_male)
                    HotelContract.GuestEntry.GENDER_FEMALE -> resources.getString(R.string.gender_female)
                    else -> resources.getString(R.string.gender_unknown)
                }
                itemView.item_gender.text = resources.getString(R.string.item_gender,gender)
                itemView.setOnClickListener(clickListener)
            }
        }

        companion object{
            val clickListener = View.OnClickListener { v ->
                val id = v.tag as Long
                val editorIntent = Intent(v.context, EditorActivity::class.java).apply {
                    action = EditorActivity.ACTION_EDIT_GUEST
                    putExtra(EditorActivity.KEY_GUEST_ID, id)
                }
                v.context.startActivity(editorIntent)
            }

            fun from(parent : ViewGroup) : ViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.list_item,parent,false)
                return ViewHolder(view)
            }
        }
    }

    fun swapCursor(cursor : Cursor?){
        if(this.cursor == cursor){
            return
        }
        this.cursor?.close()
        this.cursor = cursor
        notifyDataSetChanged()
    }
}