package org.kodluyoruz.milliyet_watchface

import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.*
import java.util.*

class ttsActivity : AppCompatActivity(), TextToSpeech.OnInitListener, DataClient.OnDataChangedListener {
    private val textToSpeech by lazy { TextToSpeech(this, this) }


    private var text: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        text = intent.getStringExtra("text")
        textToSpeech.setPitch(1.0F)
        textToSpeech.setSpeechRate(1.0F)

        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                // Speaking started.

            }

            override fun onDone(utteranceId: String) {
                // Speaking stopped.
                val dataMapRequest = PutDataMapRequest.create("/tts_done")
                dataMapRequest.dataMap.putBoolean("tts_done", true)
                dataMapRequest.dataMap.putLong("time", Date().time)
                val request = dataMapRequest.asPutDataRequest()
                request.setUrgent()

                val dataItemTask = Wearable.getDataClient(this@ttsActivity).putDataItem(request)
                dataItemTask.addOnSuccessListener { dataItem -> Log.d("TTS activity", "TTS done request send: $dataItem") }

            }

            override fun onError(utteranceId: String?) {

            }

        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(text)
        } else {
            ttsUnder20(text)
        }

    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onInit(status: Int) {

        when (status) {

            TextToSpeech.SUCCESS -> {

                val result = textToSpeech.setLanguage(Locale("tr", "TR"))
//                val result = textToSpeech.setLanguage(Locale.FRANCE)

                when (result) {

                    TextToSpeech.LANG_MISSING_DATA,
                    TextToSpeech.LANG_NOT_SUPPORTED -> {

                        Toast.makeText(this, "This Language is not supported", Toast.LENGTH_SHORT).show()
                    }

                    else -> {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ttsGreater21(text)
                        } else {
                            ttsUnder20(text)
                        }
                    }
                }
            }

            else -> {

                Log.e("TTS", "Initilization Failed!")
            }
        }
    }

    private fun ttsUnder20(text: String) {

        val hashMap = HashMap<String, String>()
        hashMap[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "MessageId"
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, hashMap)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun ttsGreater21(text: String) {

        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID)
    }

    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {

        for (event in dataEventBuffer) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path: String = event.dataItem.uri.path

                if (path.equals("/cutoff_voice", ignoreCase = true)) {
                    val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                    val cutOff = dataMapItem.dataMap.getBoolean("cutoff_voice")
                    if (cutOff) {
                        textToSpeech.stop()
                    }
                }
            }
        }
    }
}
