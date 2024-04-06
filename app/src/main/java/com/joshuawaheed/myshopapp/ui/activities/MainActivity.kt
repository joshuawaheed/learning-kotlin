package com.joshuawaheed.myshopapp.ui.activities

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.utils.Constants

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences(
            Constants.MYSHOPAPP_PREFERENCES,
            Context.MODE_PRIVATE
        )

        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        val tvMain: TextView = findViewById(R.id.tv_main)
        tvMain.text = "Hello $username."
    }
}