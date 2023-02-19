package com.joshuawaheed.a7minuteworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import com.joshuawaheed.a7minuteworkout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.flStart?.setOnClickListener {
            val intent = Intent(this, ExerciseActivity::class.java)
            startActivity(intent)
        }

        binding?.btnDemoCountdown?.setOnClickListener {
            val intent = Intent(this, DemoCountdown::class.java)
            startActivity(intent)
        }

        binding?.btnDemoTextToSpeech?.setOnClickListener {
            val intent = Intent(this, DemoTextToSpeech::class.java)
            startActivity(intent)
        }

        binding?.btnDemoViewBindingRecyclerView?.setOnClickListener {
            val intent = Intent(this, DemoViewBindingRecyclerView::class.java)
            startActivity(intent)
        }

        binding?.flBmi?.setOnClickListener {
            val intent = Intent(this, BmiActivity::class.java)
            startActivity(intent)
        }

        binding?.flHistory?.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        binding?.btnDemoRoom?.setOnClickListener {
            val intent = Intent(this, DemoRoomActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}