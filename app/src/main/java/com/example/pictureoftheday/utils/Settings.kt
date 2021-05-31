package com.example.pictureoftheday.utils

import android.content.SharedPreferences

object Settings {
    const val EARTH_PHOTO_QUANTITY = 3
    private const val THEME_KEY = "THEME_KEY"
    private lateinit var preferences: SharedPreferences

    var theme: Theme
        get() = Theme.valueOf(preferences.getString(THEME_KEY, Theme.DEFAULT.name)!!)
        set(value) = preferences.edit().putString(THEME_KEY, value.name).apply()

    fun setPreferences(preferences: SharedPreferences) {
        this.preferences = preferences
    }
}