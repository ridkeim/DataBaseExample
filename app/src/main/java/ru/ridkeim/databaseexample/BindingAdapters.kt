package ru.ridkeim.databaseexample

import android.widget.TextView
import androidx.databinding.BindingAdapter
import ru.ridkeim.databaseexample.data.Guest

@BindingAdapter(value = ["genderString","genderResId"],requireAll = false)
fun convertIdToGenderString(view : TextView, gender : Int?, resId : Int){
    val resources = view.resources
    val genderString = when (gender?: Guest.GENDER_UNKNOWN) {
        Guest.GENDER_FEMALE -> resources.getString(R.string.gender_female)
        Guest.GENDER_MALE -> resources.getString(R.string.gender_male)
        else -> resources.getString(R.string.gender_unknown)
    }
    view.text = if(resId !=0){
        resources.getString(resId,genderString)
    }else{
        genderString
    }
}

@BindingAdapter(value=["resValue","resId"],requireAll = false)
fun concatResourceString(view : TextView, resValue: String? ,resId : Int){
    resValue?.let {
        view.text = if(resId !=0){
            view.resources.getString(resId,resValue)
        }else{
            resValue
        }
    }
}

@BindingAdapter(value=["resValue","resId"],requireAll = false)
fun concatResourceValue(view : TextView, resValue: Any? ,resId : Int){
    val value = resValue?.toString() ?: ""
    view.text = if(resId !=0){
        view.resources.getString(resId,value)
    }else{
        value
    }

}