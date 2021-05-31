package com.example.pictureoftheday.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pictureoftheday.BuildConfig
import com.example.pictureoftheday.model.NetData
import com.example.pictureoftheday.model.PODRetrofitImpl
import com.example.pictureoftheday.model.earth.EarthDateServerResponseData
import com.example.pictureoftheday.model.earth.EarthPhotoServerResponseData
import com.example.pictureoftheday.utils.Settings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class EarthViewModel(
    val liveData: MutableLiveData<NetData> = MutableLiveData(),
    private val retrofitImpl: PODRetrofitImpl = PODRetrofitImpl()
) : ViewModel() {

    private val resultData = sortedMapOf<String, String>(Collections.reverseOrder())
    private val apiKey = BuildConfig.NASA_API_KEY
    private val retrofit = retrofitImpl.getRetrofitImpl()

    fun getPhotos() {
        liveData.value = NetData.Loading(null)
        if (apiKey.isBlank()) {
            liveData.value = NetData.Error(Throwable("You need API key"))
        } else {
            retrofit.getDatesForEarth(apiKey)
                .enqueue(object :
                    Callback<List<EarthDateServerResponseData>> {
                    override fun onResponse(
                        call: Call<List<EarthDateServerResponseData>>,
                        response: Response<List<EarthDateServerResponseData>>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            getPhotosPathes(
                                response.body()!!.subList(0, Settings.EARTH_PHOTO_QUANTITY)
                            )
                        } else {
                            val message = response.message()
                            if (message.isNullOrEmpty()) {
                                liveData.value =
                                    NetData.Error(Throwable("Unidentified error"))
                            } else {
                                liveData.value =
                                    NetData.Error(Throwable(message))
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<List<EarthDateServerResponseData>>,
                        t: Throwable
                    ) {
                        liveData.value = NetData.Error(t)
                    }
                })
        }
    }

    private fun getPhotosPathes(dates: List<EarthDateServerResponseData>) {

        for (date in dates) {
            val datePath = date.date!!
            retrofit.getEarthPhotos(datePath, apiKey)
                .enqueue(object :
                    Callback<List<EarthPhotoServerResponseData>> {
                    override fun onResponse(
                        call: Call<List<EarthPhotoServerResponseData>>,
                        response: Response<List<EarthPhotoServerResponseData>>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            addToResult(datePath, response.body()!![0].image!!)
                        } else {
                            val message = response.message()
                            if (message.isNullOrEmpty()) {
                                liveData.value =
                                    NetData.Error(Throwable("Unidentified error"))
                            } else {
                                liveData.value =
                                    NetData.Error(Throwable(message))
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<List<EarthPhotoServerResponseData>>,
                        t: Throwable
                    ) {
                        liveData.value = NetData.Error(t)
                    }
                })
        }


    }

    private fun addToResult(date: String, path: String) {
        val datePath = date.replace('-', '/')
        resultData[date] =
            String.format("https://api.nasa.gov/EPIC/archive/natural/$datePath/png/$path.png?api_key=$apiKey")

        liveData.value = NetData.Success(resultData.toList())
//        if (resultData.size == Settings.EARTH_PHOTO_QUANTITY) {
//            liveData.value = NetData.Success(resultData.toList())
//        }
    }

}
