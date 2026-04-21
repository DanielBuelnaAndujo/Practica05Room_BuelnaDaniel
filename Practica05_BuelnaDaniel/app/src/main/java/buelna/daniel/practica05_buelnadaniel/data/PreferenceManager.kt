package buelna.daniel.practica05_buelnadaniel.data

import android.content.Context
import androidx.core.content.edit

class PreferenceManager(context: Context){
    private val sharedPreferences = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

    fun saveLoginStatus(isLoggedIn: Boolean){
        sharedPreferences.edit { putBoolean("isLoggedIn", true) }
    }

    fun isLoggedIn(): Boolean{
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun logout(){
        //Solo una veriable
        //sharedPreferences.edit { remove("isLoggedIn").apply() }
        sharedPreferences.edit { clear().apply() }
    }


}