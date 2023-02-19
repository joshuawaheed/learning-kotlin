package com.joshuawaheed.a7minuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import com.joshuawaheed.a7minuteworkout.databinding.ActivityDemoCountdownBinding

class DemoCountdown : AppCompatActivity() {
    private var binding : ActivityDemoCountdownBinding? = null
    private var countdownTimer : CountDownTimer? = null
    private var timerDuration : Long = 60000
    private var pauseOffset : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setEventListeners()
        setTimer(timerDuration)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun pauseTimer() {
        countdownTimer?.cancel()
        countdownTimer = null
    }

    private fun resetTimer() {
        countdownTimer?.cancel()
        countdownTimer = null
        pauseOffset = 0
        setTimer(timerDuration)
    }

    private fun setBinding() {
        binding = ActivityDemoCountdownBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.tbDemoCountdown)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setEventListeners() {
        binding?.btnPause?.setOnClickListener {
            pauseTimer()
        }

        binding?.btnStart?.setOnClickListener {
            startTimer(pauseOffset)
        }

        binding?.btnReset?.setOnClickListener {
            resetTimer()
        }

        binding?.tbDemoCountdown?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setTimer(duration: Long) {
        binding?.tvTimer?.text = (duration / 1000).toString()
    }

    private fun startTimer(pauseOffsetL: Long) {
        if (countdownTimer != null) {
            return
        }

        val millisInFuture = timerDuration - pauseOffsetL
        val countDownInterval: Long = 1000

        countdownTimer = object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onFinish() {
                val toastContext = this@DemoCountdown
                val toastText = "Timer is finished"
                val toastDuration = Toast.LENGTH_LONG
                Toast.makeText(toastContext, toastText, toastDuration)
            }

            override fun onTick(millisUntilFinished: Long) {
                pauseOffset = timerDuration - millisUntilFinished
                setTimer(millisUntilFinished)
            }
        }.start()
    }
}