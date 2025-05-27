package com.example.barta.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class STTController(
    private val context: Context,
    private val onCommandDetected: (String) -> Unit,
    private val onListeningText: (String) -> Unit,
    private val onWakeWordDetected: () -> Unit
) {
    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }

    private var isCommandMode = false
    private var isListening = false
    private var handler: Handler? = Handler(context.mainLooper)
    private var commandTimeoutRunnable: Runnable? = null

    fun startListening() {
        if (!isListening) {
            isListening = true
            speechRecognizer.setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    isListening = false
                    val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.joinToString(" ") ?: ""
                    onListeningText(text)
                    processRecognition(text)
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val text = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.joinToString(" ") ?: ""
                    onListeningText(text)
                }

                override fun onError(error: Int) {
                    isListening = false
                    handler?.postDelayed({ startListening() }, 500)  // 딜레이 후 재시작
                }

                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            speechRecognizer.startListening(intent)
        }
    }

    private fun processRecognition(text: String) {
        if (!isCommandMode && text.contains("바르타", ignoreCase = true)) {
            isCommandMode = true
            onWakeWordDetected()
            startCommandMode()
        } else if (isCommandMode) {
            onCommandDetected(text)
        }

        if (!isCommandMode) {
            handler?.postDelayed({ startListening() }, 500)  // 기본 모드로 재시작
        }
    }

    private fun startCommandMode() {
        commandTimeoutRunnable?.let { handler?.removeCallbacks(it) }  // 기존 타임아웃 제거
        commandTimeoutRunnable = Runnable {
            isCommandMode = false
            startListening()
        }
        handler?.postDelayed(commandTimeoutRunnable!!, 7000)  // 7초 명령어 모드 유지
        handler?.postDelayed({ startListening() }, 500)  // 명령어 모드에서도 재인식 유지
    }

    fun stopListening() {
        speechRecognizer.stopListening()
        isListening = false
    }

    fun destroy() {
        speechRecognizer?.destroy()
        handler?.removeCallbacks(commandTimeoutRunnable ?: return)
    }
}
