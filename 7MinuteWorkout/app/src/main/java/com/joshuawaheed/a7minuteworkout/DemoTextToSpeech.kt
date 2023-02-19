package com.joshuawaheed.a7minuteworkout

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.joshuawaheed.a7minuteworkout.databinding.ActivityDemoTextToSpeechBinding
import java.util.*

class DemoTextToSpeech : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding: ActivityDemoTextToSpeechBinding? = null
    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_text_to_speech)
        setBindings()

        textToSpeech = TextToSpeech(this, this)

        binding?.btnSpeak?.setOnClickListener {
            if (binding?.etEnteredText?.text!!.isEmpty()) {
                var toastContext = this@DemoTextToSpeech
                var toastMessage = "Please enter a text to speak"
                var toastDuration = Toast.LENGTH_LONG
                Toast.makeText(toastContext, toastMessage, toastDuration).show()
            } else {
                speakOut(binding?.etEnteredText?.text.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (textToSpeech != null) {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }

        binding = null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.UK)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TextToSpeech", "The language specified is not supported!");
            }
        } else {
            Log.e("TextToSpeech", "Initialisation failed!")
        }
    }

    private fun setBindings() {
        binding = ActivityDemoTextToSpeechBinding.inflate(layoutInflater)

        setContentView(binding?.root)
        setSupportActionBar(binding?.tbExercise)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding?.tbExercise?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun speakOut(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH,null,null);
    }
}