package com.example.pictureoftheday.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pictureoftheday.BuildConfig
import com.example.pictureoftheday.model.PODRetrofitImpl
import com.example.pictureoftheday.model.PODServerResponseData
import com.example.pictureoftheday.model.NetData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

private const val DATE_FORMAT = "yyyy-MM-dd"

class PictureOfTheDayViewModel(
    val liveData: MutableLiveData<NetData> = MutableLiveData(),
    private val retrofitImpl: PODRetrofitImpl = PODRetrofitImpl()
) :
    ViewModel() {

    private val dateFormat = SimpleDateFormat(DATE_FORMAT)

    fun getImage(date: Date = Date()) {
        liveData.value = NetData.Loading(null)
        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            liveData.value = NetData.Error(Throwable("You need API key"))
        } else {
            retrofitImpl.getRetrofitImpl().getPictureOfTheDay(apiKey, dateFormat.format(date)).enqueue(object :
                Callback<PODServerResponseData> {
                override fun onResponse(
                    call: Call<PODServerResponseData>,
                    response: Response<PODServerResponseData>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        liveData.value =
                            NetData.Success(response.body()!!)
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

                override fun onFailure(call: Call<PODServerResponseData>, t: Throwable) {
                    liveData.value = NetData.Error(t)
                }
            })
        }
    }
}
