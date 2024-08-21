package com.sanjidaahmed.educationalassistant

import com.sanjidaahmed.educationalassistant.model.HuggingFaceRequest
import com.sanjidaahmed.educationalassistant.model.HuggingFaceResponse
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sanjidaahmed.educationalassistant.network.ApiClient
import com.sanjidaahmed.educationalassistant.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tvResult: TextView
    private lateinit var btnSpeak: Button
    private lateinit var textToSpeech: TextToSpeech
    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvResult = findViewById(R.id.tvResult)
        btnSpeak = findViewById(R.id.btnSpeak)
        textToSpeech = TextToSpeech(this, this)

        btnSpeak.setOnClickListener {
            startVoiceRecognition()
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD")
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "আপনার প্রশ্ন বলুন")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val question = result?.get(0).toString()
                    tvResult.text = question
                    fetchAnswer(question)
                }
            }
        }
    }

    private fun fetchAnswer(question: String) {
        val apiService = ApiClient.getClient().create(ApiService::class.java)
        val request = HuggingFaceRequest(inputs = question) // Create HuggingFaceRequest object
        val call = apiService.getAnswer(request)

        call.enqueue(object : Callback<HuggingFaceResponse> {
            override fun onResponse(call: Call<HuggingFaceResponse>, response: Response<HuggingFaceResponse>) {
                if (response.isSuccessful) {
                    val answer = response.body()?.generated_text
                    tvResult.text = answer
                    speakAnswer(answer ?: "উত্তর পাওয়া যায়নি")
                } else {
                    tvResult.text = "উত্তর পাওয়া যায়নি"
                }
            }

            override fun onFailure(call: Call<HuggingFaceResponse>, t: Throwable) {
                tvResult.text = "Failed to get answer: ${t.message}"
            }
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale("bn", "BD"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tvResult.text = "Press 'Speak' and talk!"
            }
        } else {
            tvResult.text = "Initialization Failed!"
        }
    }

    private fun speakAnswer(answer: String) {
        textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
}

