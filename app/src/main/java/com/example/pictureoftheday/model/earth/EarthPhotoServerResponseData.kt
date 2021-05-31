package com.example.pictureoftheday.model.earth

import com.google.gson.annotations.SerializedName

data class EarthPhotoServerResponseData(
    @field:SerializedName("image") val image: String?
)
