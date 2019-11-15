package com.supinternet.aqi.ui.screens.intro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.supinternet.aqi.R
import com.supinternet.aqi.ui.screens.main.MainActivityIntent
import kotlinx.android.synthetic.main.activity_intro.*

@Suppress("FunctionName")
fun Context.IntroActivityIntent(): Intent {
    return Intent(this, IntroActivity::class.java)
}

class IntroActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        intro_login_button.setOnClickListener {
            // TODO Connexion Google
            startActivity(MainActivityIntent())
        }
    }

}