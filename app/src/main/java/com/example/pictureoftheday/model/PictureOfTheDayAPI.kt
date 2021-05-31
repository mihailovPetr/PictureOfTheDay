package com.example.pictureoftheday.model

import com.example.pictureoftheday.model.earth.EarthDateServerResponseData
import com.example.pictureoftheday.model.earth.EarthPhotoServerResponseData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PictureOfTheDayAPI {

    @GET("planetary/apod")
    fun getPictureOfTheDay(@Query("api_key") apiKey: String): Call<PODServerResponseData>

    @GET("planetary/apod")
    fun getPictureOfTheDay(
        @Query("api_key") apiKey: String,
        @Query("date") date: String
    ): Call<PODServerResponseData>

    @GET("EPIC/api/natural/date/{date}")
    fun getEarthPhotos(
        @Path("date") date: String,
        @Query("api_key") apiKey: String
    ): Call<List<EarthPhotoServerResponseData>>

    @GET("EPIC/api/natural/all")
    fun getDatesForEarth(
        @Query("api_key") apiKey: String
    ): Call<List<EarthDateServerResponseData>>
}
