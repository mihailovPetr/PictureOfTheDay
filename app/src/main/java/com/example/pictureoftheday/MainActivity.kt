package com.example.pictureoftheday

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pictureoftheday.utils.Settings
import com.example.pictureoftheday.view.earth.EarthFragment
import com.example.pictureoftheday.view.PictureOfTheDayFragment
import com.example.pictureoftheday.view.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

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

        initBottomNavigationView()
    }

    private fun initBottomNavigationView() {
        findViewById<BottomNavigationView>(R.id.bottom_navigation_view).apply {
            setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.bottom_view_home -> {
                        openPage(PictureOfTheDayFragment.newInstance())
                        true
                    }
                    R.id.bottom_view_earth -> {
                        openPage(EarthFragment.newInstance())
                        true
                    }
                    R.id.bottom_view_settings -> {
                        openPage(SettingsFragment.newInstance())
                        true
                    }
                    else -> false
                }
            }

            setOnNavigationItemReselectedListener {}
        }



    }

    private fun openPage(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment).addToBackStack(null)
            .commit()
    }
}