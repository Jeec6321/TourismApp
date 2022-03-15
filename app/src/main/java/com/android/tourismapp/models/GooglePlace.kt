package com.android.tourismapp.models

import com.android.tourismapp.models.apiResponses.PlaceDetail
import com.google.gson.annotations.SerializedName

class GooglePlace(
    @SerializedName("place_id") val placeId: String,
    @SerializedName("name") val name: String,
    @SerializedName("geometry") val geometry: Geometry,
    @SerializedName("types") val types: ArrayList<String>,
    @SerializedName("bussiness_status") val bussinessStatus: String,
    @SerializedName("opening_hours") val openingHours: OpeningHours,
    var faved: Boolean = false,
    var details: PlaceDetail
) {

    fun getDistancePlace(latitud: Double, longiude: Double): Float{
        var result = FloatArray(1)

        android.location.Location.distanceBetween(
            latitud, longiude,
            geometry.location.lat, geometry.location.lng,
            result
        )

        return result.get(0) / 1000
    }
}

class Geometry(
    @SerializedName("location") val location: Location
)

class Location(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)

class OpeningHours(
    @SerializedName("open_now") val openNow: Boolean
)
