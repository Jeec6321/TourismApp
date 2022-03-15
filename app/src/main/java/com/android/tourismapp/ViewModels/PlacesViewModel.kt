package com.android.tourismapp.ViewModels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.tourismapp.Managers.SharedPreferencesManager
import com.android.tourismapp.models.GooglePlace

class PlacesViewModel: ViewModel() {

    private val _places = MutableLiveData<ArrayList<GooglePlace>>().apply {
        value = ArrayList()
    }
    val places: LiveData<ArrayList<GooglePlace>> = _places

    fun setPlaces(places: ArrayList<GooglePlace>) {
        this._places.value = places
    }

    fun addFavPlace(activity: Activity, place: GooglePlace){
        _places.value?.forEach {
            if(it.name == place.name){
                it.faved = true
            }
        }

        _places.postValue(_places.value)

        val places = SharedPreferencesManager().getFavPlaces(activity)

        places.add(place)

        SharedPreferencesManager().setFavPlaces(activity, places)
    }

    fun removeFavPlace(activity: Activity, place: GooglePlace){
        _places.value?.forEach {
            if(it.name == place.name){
                it.faved = false
            }
        }

        _places.postValue(_places.value)

        val places = SharedPreferencesManager().getFavPlaces(activity)

        run lit@{
            places.forEach {
                if(it.name == place.name){
                    places.remove(it)
                    SharedPreferencesManager().setFavPlaces(activity, places)
                    return@lit
                }
            }
        }
    }

    fun putPlaceDetails(activity: Activity, place: GooglePlace){
        _places.value?.forEach {
            if(it.name == place.name){
                it.details = place.details
            }
        }

        _places.postValue(_places.value)

        val places = SharedPreferencesManager().getFavPlaces(activity)

        run lit@{
            places.forEach {
                if(it.name == place.name){
                    places.remove(it)
                    places.add(place)
                    SharedPreferencesManager().setFavPlaces(activity, places)
                    return@lit
                }
            }
        }
    }

    //--------------------------------------------------------------------------------------
    private val _latitude = MutableLiveData<Double>().apply {
        value = 0.0
    }
    val latitude: LiveData<Double> = _latitude

    fun setLatitude(latitude: Double) {
        this._latitude.value = latitude
    }
    //--------------------------------------------------------------------------------------
    private val _longitude = MutableLiveData<Double>().apply {
        value = 0.0
    }
    val longitude: LiveData<Double> = _longitude

    fun setLongitude(longitude: Double) {
        this._longitude.value = longitude
    }
    //--------------------------------------------------------------------------------------
}