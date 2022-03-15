package com.android.tourismapp.interfaces

import com.android.tourismapp.models.apiResponses.ApiPlaceDetails
import com.android.tourismapp.models.apiResponses.ApiPlacesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface Service {

    @GET
    suspend fun getPlaces(
        @Url url: String,
        @Query("key") key: String,
        @Query("location") location: String,
        @Query("radius") radius: Double,
        @Query("type") type: String
    ) : Response<ApiPlacesResponse>

    @GET
    suspend fun getPlaceDestails(
        @Url url: String,
        @Query("key") key: String,
        @Query("place_id") location: String,
    ) : Response<ApiPlaceDetails>

}