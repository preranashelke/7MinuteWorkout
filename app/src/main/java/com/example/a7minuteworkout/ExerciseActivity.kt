package com.example.a7minuteworkout

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_exercise.*
import java.util.*

class ExerciseActivity : AppCompatActivity(),TextToSpeech.OnInitListener{


    private var restTimer:CountDownTimer?=null
    private  var restProgress=0

    private var exerciseTimer:CountDownTimer?=null
    private  var exerciseProgress=0

    private var exerciseList:ArrayList<ExerciseModel>?=null
    private var currentExercisePosition=-1

    private var tts:TextToSpeech?=null
    private var player:MediaPlayer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)
        setSupportActionBar(toolbar_exercise_activity)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        toolbar_exercise_activity.setNavigationOnClickListener {
            onBackPressed()
        }
        tts= TextToSpeech(this,this)
        exerciseList=Constants.defaultExerciseList()
        setupRestView()

    }

    override fun onDestroy() {
        if(restTimer!=null){
            restTimer!!.cancel()
            restProgress=0
        }
        if (exerciseTimer!=null){
            exerciseTimer!!.cancel()
            exerciseProgress=0
        }
        if(tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }
        if(player!=null){
            player!!.stop()
        }
        super.onDestroy()
    }

    private  fun setRestProgressBar(){
        progressBar.progress=restProgress
        restTimer= object :CountDownTimer(10000,1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                progressBar.progress=10-restProgress
                tvTimer.text=(10-restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++
              setupExerciseView()

            }
        }.start()
    }
    private  fun setExerciseProgressBar(){
        progressBarExercise.progress=exerciseProgress
        exerciseTimer= object :CountDownTimer(30000,1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                progressBarExercise.progress=30-exerciseProgress
                tvExerciseTimer.text=(30-exerciseProgress).toString()
            }

            override fun onFinish() {
               if (currentExercisePosition<exerciseList?.size!!-1){
                   setupRestView()
               }else{
                   Toast.makeText(this@ExerciseActivity,
                   "congratulations you have completed 7 minutes activity",
                   Toast.LENGTH_SHORT).show()
               }
            }
        }.start()
    }
    private  fun setupExerciseView(){
        llRestView.visibility= View.GONE
        llExerciseView.visibility=View.VISIBLE
        if(exerciseTimer!=null){
            exerciseTimer!!.cancel()
            exerciseProgress=0
        }
        speakOut(exerciseList!![currentExercisePosition].getName())
        setExerciseProgressBar()
        ivImage.setImageResource(exerciseList!![currentExercisePosition].getImage())
        tvExerciseName.text=exerciseList!![currentExercisePosition].getName()
    }
    private  fun setupRestView(){
          try {
              player=MediaPlayer.create(applicationContext,R.raw.press_start)
              player!!.isLooping=false
              player!!.start()
          }catch (e:Exception){
              e.printStackTrace()
          }


        llRestView.visibility= View.VISIBLE
        llExerciseView.visibility=View.GONE
        if(restTimer!=null){
            restTimer!!.cancel()
            restProgress=0
        }

        tvUpcomingExerciseName.text=exerciseList!![currentExercisePosition+1].getName()
        setRestProgressBar()

    }

    override fun onInit(status: Int) {
       if(status==TextToSpeech.SUCCESS){
           val result=tts!!.setLanguage(Locale.US)
           if(result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED){
             Log.e("TTS","LANGAUGE SPECIFIED IS NOT SUPPORTED")
           }
       }else{
           Log.e("TTS","Initialization failed")
       }
    }
    private fun speakOut(text:String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }
}