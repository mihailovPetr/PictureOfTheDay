package com.example.pictureoftheday.model.earth

import com.google.gson.annotations.SerializedName

data class EarthDateServerResponseData(
    @field:SerializedName("date") val date: String?
)
