package com.supinternet.aqi.ui.screens.splashscreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.supinternet.aqi.ui.screens.intro.IntroActivityIntent
import com.supinternet.aqi.ui.screens.main.MainActivityIntent

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isUserConnected()) {
            startActivity(MainActivityIntent())
        } else {
            startActivity(IntroActivityIntent())
        }

        finish()
    }

    private fun isUserConnected() : Boolean {
        // TODO Rechercher si l'utilisateur est déjà connecté
        return false
    }
}