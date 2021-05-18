package com.example.pictureoftheday

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pictureoftheday.utils.Settings
import com.example.pictureoftheday.view.PictureOfTheDayFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Settings.setPreferences(getPreferences(Context.MODE_PRIVATE))
        setTheme(Settings.theme.id)

        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PictureOfTheDayFragment.newInstance())
                .commitNow()
        }
    }
}