package com.example.pictureoftheday.model

sealed class NetData {
    data class Success<T>(val data: T) : NetData()
    data class Error(val error: Throwable) : NetData()
    data class Loading(val progress: Int?) : NetData()
}
