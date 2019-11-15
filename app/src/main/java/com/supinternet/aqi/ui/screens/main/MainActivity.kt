package com.supinternet.aqi.ui.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.supinternet.aqi.R
import com.supinternet.aqi.ui.screens.main.tabs.aroundme.MapsTab
import com.supinternet.aqi.ui.screens.main.tabs.favs.FavsTab
import com.supinternet.aqi.ui.screens.main.tabs.settings.SettingsTab
import com.supinternet.aqi.ui.screens.main.tabs.travel.TravelTab
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

@Suppress("FunctionName")
fun Context.MainActivityIntent(): Intent {
    return Intent(this, MainActivity::class.java)
}


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(MapsTab())

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            replaceFragment(
                when (item.itemId) {
                    R.id.around_me -> MapsTab()
                    R.id.favs -> FavsTab()
                    R.id.travel -> TravelTab()
                    R.id.settings -> SettingsTab()
                    else -> throw Exception("Unknown menu item")
                }
            )

            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val tag = fragment.javaClass.simpleName

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.main_content,
                supportFragmentManager.findFragmentByTag(tag) ?: fragment,
                tag
            )
            .commitAllowingStateLoss()
    }

}
