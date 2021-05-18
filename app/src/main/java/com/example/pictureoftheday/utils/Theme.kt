package com.example.pictureoftheday.utils

import com.example.pictureoftheday.R

enum class Theme(val displayingName: String, val id: Int) {
    DEFAULT("Default", R.style.Theme_PictureOfTheDay),
    RED("Red", R.style.RedTheme),
    BLUE("Blue", R.style.BlueTheme)
}