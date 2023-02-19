package com.joshuawaheed.a7minuteworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.joshuawaheed.a7minuteworkout.databinding.ActivityExerciseBinding
import com.joshuawaheed.a7minuteworkout.databinding.DialogCustomBackConfirmationBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding : ActivityExerciseBinding? = null
    private var currentExercisePosition = -1
    private var exerciseAdapter: ExerciseStatusAdapter? = null
    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var mediaPlayer: MediaPlayer? = null
    private var restTimerExercise: CountDownTimer? = null
    private var restTimerExerciseDuration: Long = 1
    private var restTimerGetReady: CountDownTimer? = null
    private var restTimerGetReadyDuration: Long = 1
    private var restProgressExercise = 0
    private var restProgressGetReady = 0
    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExerciseBinding.inflate(layoutInflater)

        setContentView(binding?.root)
        setSupportActionBar(binding?.tbExercise)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.tbExercise?.setNavigationOnClickListener {
            customDialogOnBackButton()
        }

        textToSpeech = TextToSpeech(this, this)

        exerciseList = Constants.defaultExerciseList()

        setupRestView()
        setupExerciseStatusRecyclerView()
    }

    private fun setupRestView() {
        try {
            val namespace = "com.joshuawaheed.a7minuteworkout"
            val uriString = "android.resource://$namespace/${R.raw.press_start}"
            val soundUri = Uri.parse(uriString)
            mediaPlayer = MediaPlayer.create(applicationContext, soundUri)
            mediaPlayer?.isLooping = false
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding?.flProgressBarGetReady?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExercise?.visibility = View.INVISIBLE
        binding?.flProgressBarExercise?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE

        if (restTimerGetReady != null) {
            restProgressGetReady = 0
            restTimerGetReady!!.cancel()
        }

        binding?.tvUpcomingExerciseName?.text = exerciseList!![currentExercisePosition + 1].getName()

        setRestProgressBar()
    }

    private fun setRestProgressBar() {
        binding?.progressBarGetReady?.progress = restProgressGetReady

        val millisInFuture: Long = restTimerGetReadyDuration * 1000
        val countDownInterval: Long = 1000

        restTimerGetReady = object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                restProgressGetReady++

                val progress = restTimerGetReadyDuration.toInt() - restProgressGetReady
                binding?.progressBarGetReady?.progress = progress
                binding?.tvTimerGetReady?.text = progress.toString()
            }

            override fun onFinish() {
                val toastContext = this@ExerciseActivity
                val toastMessage = "Here now we will start the exercise"
                val toastLength = Toast.LENGTH_LONG
                Toast.makeText(toastContext, toastMessage, toastLength).show()

                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setupExerciseView()
            }
        }.start()
    }

    private fun setupExerciseView() {
        binding?.flProgressBarGetReady?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExercise?.visibility = View.VISIBLE
        binding?.flProgressBarExercise?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.tvUpcomingLabel?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE

        if (restTimerExercise != null) {
            restProgressExercise = 0
            restTimerGetReady!!.cancel()
        }

        speakOut(exerciseList!![currentExercisePosition].getName())
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExercise?.text = exerciseList!![currentExercisePosition].getName()
        setExerciseProgressBar()
    }

    private fun setExerciseProgressBar() {
        binding?.progressBarExercise?.progress = restProgressExercise

        val millisInFuture: Long = restTimerExerciseDuration * 1000
        val countDownInterval: Long = 1000

        restTimerExercise = object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                restProgressExercise++

                val progress = restTimerExerciseDuration.toInt() - restProgressExercise
                binding?.progressBarExercise?.progress = progress
                binding?.tvTimerExercise?.text = progress.toString()
            }

            override fun onFinish() {
                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    val toastContext = this@ExerciseActivity
                    val toastMessage = "30 seconds are over, let's go to the rest view"
                    val toastLength = Toast.LENGTH_LONG
                    Toast.makeText(toastContext, toastMessage, toastLength).show()

                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyItemChanged(currentExercisePosition)

                    setupRestView()
                } else {
                    val toastContext = this@ExerciseActivity
                    val toastMessage = "Congratulations, you have completed the 7 minutes workout"
                    val toastLength = Toast.LENGTH_LONG
                    Toast.makeText(toastContext, toastMessage, toastLength).show()

                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }.start()
    }

    override fun onDestroy() {
        if (restTimerExercise != null) {
            restTimerExercise?.cancel()
            restProgressExercise = 0
        }

        if (restTimerGetReady != null) {
            restTimerGetReady?.cancel()
            restProgressGetReady = 0
        }

        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }

        if(mediaPlayer != null){
            mediaPlayer!!.stop()
        }

        super.onDestroy()

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

    override fun onBackPressed() {
        customDialogOnBackButton()
    }

    private fun speakOut(text: String) {
        textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun customDialogOnBackButton() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnBackConfirmationYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }

        dialogBinding.btnBackConfirmationNo.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()
    }

    private fun setupExerciseStatusRecyclerView() {
        binding?.rvExerciseStatus?.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }
}