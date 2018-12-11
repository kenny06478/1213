package com.example.user.a102501

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity:AppCompatActivity( ){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            val email = email_edittext_login.text.toString()
            val password = password_edittext_login.text.toString()
            Log.d("LoginActivity","email is" + email)
            Log.d("LoginActivity","password is"+ password)

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
        }
        back_register_login.setOnClickListener {
            finish()
        }
    }
}