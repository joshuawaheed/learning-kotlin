package com.joshuawaheed.projemanag

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_intro)

        val flagFullScreen = WindowManager.LayoutParams.FLAG_FULLSCREEN

        window.setFlags(flagFullScreen, flagFullScreen)

        val btnSignIn: Button = findViewById(R.id.btn_sign_in_intro)

        btnSignIn.setOnClickListener {
            startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
        }

        val btnSignUp: Button = findViewById(R.id.btn_sign_up_intro)

        btnSignUp.setOnClickListener {
            startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
        }
    }
}