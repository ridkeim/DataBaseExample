package ru.ridkeim.databaseexample.data

import android.widget.EditText
import androidx.databinding.InverseMethod

object GuestAgeConverter {
    @InverseMethod("stringToAge")
    @JvmStatic fun ageToString(newValue: Int) : String {
        return newValue.toString()
    }

    @JvmStatic fun stringToAge(newValue: String): Int{
        return newValue.toIntOrNull() ?: 0
    }
}