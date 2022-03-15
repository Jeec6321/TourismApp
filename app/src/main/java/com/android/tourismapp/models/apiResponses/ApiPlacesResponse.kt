package com.android.tourismapp.models.apiResponses

import com.android.tourismapp.models.GooglePlace
import com.google.gson.annotations.SerializedName

class ApiPlacesResponse(
    @SerializedName("next_page_token") val nextPageToken: String,
    @SerializedName("results") val results: ArrayList<GooglePlace>,
)