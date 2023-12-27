package com.rjornelas.countdown_timer

import android.animation.Animator.AnimatorPauseListener
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private var timeSelected: Int = 0
    private var timeCountDown: CountDownTimer? = null
    private var timeProgress = 0
    private var pauseOffset: Long = 0
    private var isStart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAdd: ImageButton = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener{
            setTimeFunction()
        }
        val startButton: Button = findViewById(R.id.btnPlayPause)
        startButton.setOnClickListener{
            startTimeSetup()
        }

        val resetButton: ImageButton = findViewById(R.id.btnReset)
        resetButton.setOnClickListener{
            timeReset()
        }

        val tvAddTime: TextView = findViewById(R.id.tvMoreTime)
        tvAddTime.setOnClickListener{
            addExtraTime()
        }
    }

    private fun addExtraTime(){
        val progressBar = findViewById<ProgressBar>(R.id.pbTimer)
        if(timeSelected!=0){
            timeSelected+15
            progressBar.max = timeSelected
            timePause()
            startTimer(pauseOffset)
            Toast.makeText(this, "15 sec added", Toast.LENGTH_LONG).show()
        }
    }

    private fun timeReset(){
        if(timeCountDown!=null){
            timeCountDown!!.cancel()
            timeProgress = 0
            timeSelected = 0
            pauseOffset=0
            timeCountDown = null
            val startBtn: Button = findViewById(R.id.btnPlayPause)
            startBtn.text = getString(R.string.start_button_text)
            isStart = true
            val progressBar = findViewById<ProgressBar>(R.id.pbTimer)
            progressBar.progress = 0
            val tvTimeLeft: TextView = findViewById(R.id.tvTimeLeft)
            tvTimeLeft.text = "0"
        }
    }

    private fun timePause(){
        if(timeCountDown!=null){
            timeCountDown!!.cancel()
        }
    }

    private fun startTimeSetup(){
        val startBtn: Button = findViewById(R.id.btnPlayPause)
        if(timeSelected>timeProgress){
            if(isStart){
                startBtn.text= getString(R.string.pause_button_text)
                startTimer(pauseOffset)
                isStart = false
            }else{
                isStart = true
                startBtn.text = getString(R.string.resume_button_text)
                timePause()
            }
        }else{
            Toast.makeText(this, "Enter time", Toast.LENGTH_LONG).show()
        }
    }

    private fun startTimer(pauseOffsetLong: Long){
        val progressBar = findViewById<ProgressBar>(R.id.pbTimer)
        progressBar.progress = timeProgress
        timeCountDown = object : CountDownTimer(
            (timeSelected*1_000).toLong() - pauseOffsetLong*1_000, 1_000){

            override fun onTick(millisUntilFinished: Long) {
                timeProgress++
                pauseOffset = timeSelected.toLong() - millisUntilFinished/1_000
                progressBar.progress = timeSelected - timeProgress
                val timeLeftTv: TextView = findViewById(R.id.tvTimeLeft)
                timeLeftTv.text = (timeSelected - timeProgress).toString()
            }

            override fun onFinish() {
                timeReset()
                Toast.makeText(this@MainActivity, "Times Up!", Toast.LENGTH_LONG).show()
            }

        }.start()
    }

    private fun setTimeFunction(){
        val timeDialog = Dialog(this)
        timeDialog.setContentView(R.layout.add_dialog)
        val timeSet = timeDialog.findViewById<EditText>(R.id.edtGetTime)
        val tvTimeLeft: TextView = findViewById(R.id.tvTimeLeft)
        val btnStart: Button = findViewById(R.id.btnPlayPause)
        val progressBar = findViewById<ProgressBar>(R.id.pbTimer)


        timeDialog.findViewById<Button>(R.id.btnOk).setOnClickListener{
            if(timeSet.text.isEmpty()){
                Toast.makeText(this, "Enter time duration", Toast.LENGTH_LONG).show()
            }else{
                timeReset()

                if(timeSet.text.toString().toLong() > 9999) {
                    timeSet.setText(getString(R.string.max_time_value))
                }

                tvTimeLeft.text = timeSet.text
                btnStart.text = getString(R.string.start_button_text)
                timeSelected = timeSet.text.toString().toInt()
                progressBar.max = timeSelected
                timeDialog.dismiss()
                startTimeSetup()
            }
        }
        timeDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(timeCountDown!=null){
            timeCountDown?.cancel()
            timeProgress=0
        }
    }
}