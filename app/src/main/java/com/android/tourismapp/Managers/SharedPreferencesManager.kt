package com.android.tourismapp.Managers

import android.app.Activity
import android.content.Context
import com.android.tourismapp.models.GooglePlace
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Exception

class SharedPreferencesManager {

    private val PLACES_FILE: String = "PLACES_FILE"
    private val PLACES_VAR: String = "PLACES_VAR"
    private val FAV_PLACES_VAR: String = "FAV_PLACES_VAR"

    fun getFavPlaces(activity: Activity): ArrayList<GooglePlace> {
        var data: ArrayList<GooglePlace> = ArrayList()

        try{
            val preferences = activity.getSharedPreferences(PLACES_FILE, Context.MODE_PRIVATE)

            val stringData = preferences.getString(FAV_PLACES_VAR, "")

            val type = object : TypeToken<ArrayList<GooglePlace>>() {}.type

            data = Gson().fromJson(stringData, type)
        } catch (e: Exception){ }

        return data
    }

    fun setFavPlaces(activity: Activity, places: ArrayList<GooglePlace>): Boolean{
        try{
            val preferences = activity.getSharedPreferences(PLACES_FILE, Context.MODE_PRIVATE)

            val editor = preferences.edit()

            editor.putString(FAV_PLACES_VAR, Gson().toJson(places))

            editor.apply()

            //editor.commit()

            return true
        } catch (e: Exception){
            return false
        }
    }

    fun getPlaces(activity: Activity): ArrayList<GooglePlace> {
        var data: ArrayList<GooglePlace> = ArrayList()

        try{
            val preferences = activity.getSharedPreferences(PLACES_FILE, Context.MODE_PRIVATE)

            val stringData = preferences.getString(PLACES_VAR, "")

            val type = object : TypeToken<ArrayList<GooglePlace>>() {}.type

            data = Gson().fromJson(stringData, type)
        } catch (e: Exception){ }

        return data
    }

    fun setPlaces(activity: Activity, places: ArrayList<GooglePlace>): Boolean{
        try{
            val preferences = activity.getSharedPreferences(PLACES_FILE, Context.MODE_PRIVATE)

            val editor = preferences.edit()

            editor.putString(PLACES_VAR, Gson().toJson(places))

            editor.apply()

            //editor.commit()

            return true
        } catch (e: Exception){
            return false
        }
    }

}