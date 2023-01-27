package com.example.fourieraudiorecorder

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException

const val REQUEST_CODE = 200

class MainActivity : AppCompatActivity() {
    private lateinit var recorder : AudioRecord
    private var bufferSize = 0
    private val sampleRate = 44100 //Hz
    private var isRecording = false

    private var permissionGranted = false
    private var permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted){
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
        }

        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnStop = findViewById<Button>(R.id.btnStop)

        btnStart.setOnClickListener{
            startRecording()
        }

        btnStop.setOnClickListener{

        }
    }

    @SuppressLint("MissingPermission")
    private fun startRecording(){
        if (!permissionGranted){
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            println("Permission not granted")
            return
        }
        try {
            bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_FLOAT)

            recorder = AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_FLOAT,
                bufferSize)

            if (recorder.state == 1){
                println("Trying to record")
                val isRecordingTextView = findViewById<TextView>(R.id.is_recording)
                isRecordingTextView.text = getString(R.string.recording)
                recorder.startRecording()
                isRecording = true
                //val data = FloatArray(bufferSize)
                val data = ByteArray(bufferSize)
                var read: Int
                while (isRecording){
                    read = recorder.read(data, 0, bufferSize)
                    if (read >= 0 ){
                        println("SUCCESSFULLY READ")
                        println(data[0])
                    } else{
                        println("UNSUCCESSFUL")
                        break
                    }
                }
            }
        } catch (_: IOException){
            println("IO Exception")
        }
    }

    private fun stopRecorder(){

    }

    private fun checkWritePermission() : Boolean {
        //val result1 : Int = ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val result2 : Int = ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.RECORD_AUDIO)
        //return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED
        return  result2 == PackageManager.PERMISSION_GRANTED
    }
    private fun requestWritePermission() {
        ActivityCompat.requestPermissions(this, arrayOf<String>(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.MODIFY_AUDIO_SETTINGS, android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
    }
}