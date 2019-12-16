package com.supinternet.aqi.ui.screens.intro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.supinternet.aqi.R
import com.supinternet.aqi.ui.screens.main.MainActivity
import kotlinx.android.synthetic.main.activity_intro.*

@Suppress("FunctionName")
fun Context.IntroActivityIntent(): Intent {
    return Intent(this, IntroActivity::class.java)
}

class IntroActivity: AppCompatActivity() {

    var fbAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        intro_login_button.setOnClickListener { view ->
            var inputEmail = findViewById(R.id.email_login) as EditText
            var inputPassword = findViewById(R.id.password_login) as EditText
            signIn(view ,inputEmail.getText().toString(), inputPassword.getText().toString())
        }

        intro_button_redirectSignUp.setOnClickListener { view ->
            startActivity(RegisterActivityIntent())
        }
    }

    fun signIn(view: View, email: String, password: String){
        showMessage(view,"Authenticating...")

        fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
            if(task.isSuccessful){
                var intent = Intent(this, MainActivity::class.java)
                intent.putExtra("id", fbAuth.currentUser?.email)
                Log.v("iddumec", fbAuth.currentUser?.uid)
                startActivity(intent)

            }else{
                showMessage(view,"Error: ${task.exception?.message}")
            }
        })
    }

    fun showMessage(view:View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
    }

}