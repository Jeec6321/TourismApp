package com.android.tourismapp.models.apiResponses

import com.android.tourismapp.models.Geometry
import com.google.gson.annotations.SerializedName

class ApiPlaceDetails (
    @SerializedName("result") val result: PlaceDetail
)

class PlaceDetail(
    @SerializedName("name") val name: String,
    @SerializedName("formatted_address") val formattedAddress: String,
    @SerializedName("formatted_phone_number") val formattedPhoneNumber: String,
    @SerializedName("geometry") val geometry: Geometry,
    @SerializedName("reviews") val reviews: ArrayList<Review>? = null
)

class Review (
    @SerializedName("author_name") val authorName: String,
    @SerializedName("author_url") val authorUrl: String,
    @SerializedName("profile_photo_url") val profilePhotoUrl: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("text") val text: String,
)