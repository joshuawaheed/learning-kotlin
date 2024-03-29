package com.joshuawaheed.projemanag.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.TextView
import com.joshuawaheed.projemanag.R
import com.joshuawaheed.projemanag.firebase.FirestoreClass

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        val flagFullScreen = WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.setFlags(flagFullScreen, flagFullScreen)

        val typeface: Typeface = Typeface.createFromAsset(assets, "carbon bl.ttf")
        val tvAppName: TextView = findViewById(R.id.tvAppName)
        tvAppName.typeface = typeface

        Handler().postDelayed({
            val currentUserId = FirestoreClass().getCurrentUserId()

            if (currentUserId.isNotEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, IntroActivity::class.java))
            }

            finish()
        }, 2500)
    }
}