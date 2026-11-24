package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class VoiceUiState(
    val isListening: Boolean = false,
    val partialTranscript: String = "",
    val rmsDb: Float = 0f,
    val isSpeaking: Boolean = false,
    val ttsReady: Boolean = false,
    val errorMessage: String? = null
)

class VoiceAssistantManager(
    private val context: Context,
    private val onFinalTranscript: (String) -> Unit
) {
    private val _state = MutableStateFlow(VoiceUiState())
    val state: StateFlow<VoiceUiState> = _state.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null

    init {
        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            val ready = status == TextToSpeech.SUCCESS
            if (ready) {
                textToSpeech?.language = Locale.getDefault()
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _state.value = _state.value.copy(isSpeaking = true)
                    }

                    override fun onDone(utteranceId: String?) {
                        _state.value = _state.value.copy(isSpeaking = false)
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _state.value = _state.value.copy(isSpeaking = false)
                    }
                })
            }
            _state.value = _state.value.copy(ttsReady = ready)
        }
    }

    fun startListening() {
        stopSpeaking()
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _state.value = _state.value.copy(
                isListening = false,
                errorMessage = "Speech recognition service is not available on this device."
            )
            return
        }

        try {
            speechRecognizer?.destroy()
            val recognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext)
            speechRecognizer = recognizer

            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    _state.value = _state.value.copy(
                        isListening = true,
                        partialTranscript = "",
                        errorMessage = null
                    )
                }

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(rmsdB: Float) {
                    _state.value = _state.value.copy(rmsDb = rmsdB.coerceIn(0f, 12f))
                }

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    _state.value = _state.value.copy(isListening = false, rmsDb = 0f)
                }

                override fun onError(error: Int) {
                    val message = when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Please try again."
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Listening timed out. Tap mic to speak again."
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
                        SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                            "Network error during voice recognition."
                        else -> "Voice recognition could not complete ($error)."
                    }
                    _state.value = _state.value.copy(
                        isListening = false,
                        rmsDb = 0f,
                        errorMessage = message
                    )
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val transcript = matches?.firstOrNull().orEmpty().trim()
                    _state.value = _state.value.copy(
                        isListening = false,
                        partialTranscript = "",
                        rmsDb = 0f,
                        errorMessage = null
                    )
                    if (transcript.isNotBlank()) {
                        onFinalTranscript(transcript)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val partial = partialResults
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        .orEmpty()
                    if (partial.isNotBlank()) {
                        _state.value = _state.value.copy(partialTranscript = partial)
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            _state.value = _state.value.copy(isListening = true, errorMessage = null)
            recognizer.startListening(intent)
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isListening = false,
                errorMessage = e.localizedMessage ?: "Unable to start voice recognition."
            )
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {
        }
        _state.value = _state.value.copy(isListening = false, rmsDb = 0f)
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun speak(rawText: String) {
        if (!_state.value.ttsReady) return
        val cleanText = rawText
            .replace(Regex("\\*\\*|__|`|#"), "")
            .replace("•", ",")
            .trim()
        if (cleanText.isBlank()) return
        textToSpeech?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "nater_reply_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        try {
            textToSpeech?.stop()
        } catch (_: Exception) {
        }
        _state.value = _state.value.copy(isSpeaking = false)
    }

    fun release() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (_: Exception) {
        }
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (_: Exception) {
        }
    }
}
