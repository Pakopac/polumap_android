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
import kotlinx.android.synthetic.main.activity_intro_register.*

@Suppress("FunctionName")
fun Context.RegisterActivityIntent(): Intent {
    return Intent(this, RegisterActivity::class.java)
}

class RegisterActivity : AppCompatActivity(){

    var fbAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_register)

        intro_button_signUp.setOnClickListener { view ->
            var inputEmail = findViewById(R.id.email_register) as EditText
            var inputPassword = findViewById(R.id.password_register) as EditText
            signUp(view ,inputEmail.getText().toString(), inputPassword.getText().toString())
        }
    }

    fun signUp(view: View, email: String, password: String) {
        showMessage(view,"Sign Up...")

        fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
            if(task.isSuccessful){
                startActivity(IntroActivityIntent())
            }else{
                showMessage(view,"Error: ${task.exception?.message}")
            }
        })
    }

    fun showMessage(view:View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
    }

}