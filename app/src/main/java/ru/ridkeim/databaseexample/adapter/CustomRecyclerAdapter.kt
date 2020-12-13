package ru.ridkeim.databaseexample.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.ridkeim.databaseexample.EditorActivity
import ru.ridkeim.databaseexample.R
import ru.ridkeim.databaseexample.data.Guest
import ru.ridkeim.databaseexample.data.HotelContract
import ru.ridkeim.databaseexample.databinding.ListItemBinding

class CustomRecyclerAdapter : RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder>() {

    private var data : List<Guest> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder private constructor(val itemBinding: ListItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item : Guest){
            val resources = itemBinding.root.resources
            itemBinding.itemName.text = resources.getString(R.string.item_name,item.name)
            itemBinding.itemCity.text = resources.getString(R.string.item_city,item.city)
            itemBinding.itemAge.text = resources.getString(R.string.item_age,item.age.toString())
            itemBinding.root.tag = item.guestId
            val gender = when(item.gender){
                HotelContract.GuestEntry.GENDER_MALE -> resources.getString(R.string.gender_male)
                HotelContract.GuestEntry.GENDER_FEMALE -> resources.getString(R.string.gender_female)
                else -> resources.getString(R.string.gender_unknown)
            }
            itemBinding.itemGender.text = resources.getString(R.string.item_gender,gender)
            itemBinding.root.setOnClickListener(clickListener)
        }

        companion object{
            val clickListener = View.OnClickListener { v ->
                val id = v.tag as Long
                val editorIntent = Intent(v.context, EditorActivity::class.java).apply {
                    putExtra(EditorActivity.KEY_GUEST_ID, id)
                }
                v.context.startActivity(editorIntent)
            }

            fun from(parent : ViewGroup) : ViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val liBinding = ListItemBinding.inflate(inflater, parent, false)
                return ViewHolder(liBinding)
            }
        }
    }

    fun submitList(data: List<Guest>?) {
        this.data = data ?: emptyList<Guest>()
        notifyDataSetChanged()
    }
}