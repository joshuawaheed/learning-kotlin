package com.joshuawaheed.projemanag

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val flagFullScreen = WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.setFlags(flagFullScreen, flagFullScreen)
        val typeface: Typeface = Typeface.createFromAsset(assets, "carbon bl.ttf")
        val tvAppName: TextView = findViewById(R.id.tvAppName)
        tvAppName.typeface = typeface
    }
}