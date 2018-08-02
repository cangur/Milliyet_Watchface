package org.kodluyoruz.milliyet_watchface

import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener

class dasd : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val speech = TextToSpeech(this, TextToSpeech.OnInitListener { })

        speech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                // Speaking started.

            }

            override fun onDone(utteranceId: String) {
                // Speaking stopped.

            }

            override fun onError(utteranceId: String) {

            }

        })
    }
}

