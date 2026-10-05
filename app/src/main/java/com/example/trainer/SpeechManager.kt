package com.example.trainer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.model.SpeakingEvaluation
import java.util.Locale

class SpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var speechRecognizer: SpeechRecognizer? = null

    init {
        try {
            tts = TextToSpeech(context, this)
        } catch (e: Exception) {
            isTtsReady = false
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            tts?.language = Locale.ENGLISH
        }
    }

    fun speak(
        text: String,
        languageCode: String,
        speechRate: Float = 1.0f,
        onStart: (() -> Unit)? = null,
        onDone: (() -> Unit)? = null
    ) {
        if (!isTtsReady || tts == null) {
            onDone?.invoke()
            return
        }

        val locale = when (languageCode) {
            "hi" -> Locale("hi", "IN")
            "mr" -> Locale("mr", "IN")
            "ja" -> Locale.JAPAN
            else -> Locale.US
        }

        tts?.apply {
            language = locale
            setSpeechRate(speechRate)
            setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    onStart?.invoke()
                }

                override fun onDone(utteranceId: String?) {
                    onDone?.invoke()
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    onDone?.invoke()
                }
            })
            speak(text, TextToSpeech.QUEUE_FLUSH, null, "UTTERANCE_${System.currentTimeMillis()}")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    fun startListening(
        languageCode: String,
        onResult: (String) -> Unit,
        onError: (String) -> Unit,
        onListeningState: (Boolean) -> Unit
    ) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition not available on this device.")
            return
        }

        try {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        onListeningState(true)
                    }

                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        onListeningState(false)
                    }

                    override fun onError(error: Int) {
                        onListeningState(false)
                        val errorMsg = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client error"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Audio permission required"
                            SpeechRecognizer.ERROR_NETWORK -> "Network error during recognition"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Recognition timeout"
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Try speaking clearly."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                            SpeechRecognizer.ERROR_SERVER -> "Server error"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
                            else -> "Recognition error: $error"
                        }
                        onError(errorMsg)
                    }

                    override fun onResults(results: Bundle?) {
                        onListeningState(false)
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val spokenText = matches?.firstOrNull() ?: ""
                        onResult(spokenText)
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                val langTag = when (languageCode) {
                    "hi" -> "hi-IN"
                    "mr" -> "mr-IN"
                    "ja" -> "ja-JP"
                    else -> "en-US"
                }
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langTag)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            onListeningState(false)
            onError(e.message ?: "Failed to start speech recognizer")
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
    }

    fun evaluateSpeech(targetSentence: String, recognizedText: String): SpeakingEvaluation {
        val cleanTarget = targetSentence.trim().lowercase(Locale.getDefault())
            .replace(Regex("[^\\p{L}\\p{Nd}\\s]"), "")
        val cleanRecognized = recognizedText.trim().lowercase(Locale.getDefault())
            .replace(Regex("[^\\p{L}\\p{Nd}\\s]"), "")

        val targetWords = cleanTarget.split(Regex("\\s+")).filter { it.isNotEmpty() }
        val recognizedWords = cleanRecognized.split(Regex("\\s+")).filter { it.isNotEmpty() }

        val missingWords = targetWords.filter { target ->
            recognizedWords.none { spoken -> spoken.contains(target) || target.contains(spoken) }
        }

        val matchingWordsCount = targetWords.count { target ->
            recognizedWords.any { spoken -> spoken.contains(target) || target.contains(spoken) }
        }

        val accuracyScore = if (targetWords.isNotEmpty()) {
            ((matchingWordsCount.toFloat() / targetWords.size.toFloat()) * 100).toInt().coerceIn(0, 100)
        } else 100

        // Levenshtein similarity for pronunciation clarity
        val levDistance = computeLevenshtein(cleanTarget, cleanRecognized)
        val maxLen = maxOf(cleanTarget.length, cleanRecognized.length).coerceAtLeast(1)
        val charSimilarity = (((maxLen - levDistance).toFloat() / maxLen.toFloat()) * 100).toInt().coerceIn(0, 100)

        val pronunciationScore = ((accuracyScore * 0.6f) + (charSimilarity * 0.4f)).toInt().coerceIn(0, 100)
        val estimatedWpm = (recognizedWords.size * 25).coerceIn(40, 160)

        val feedback = when {
            accuracyScore >= 90 -> "Outstanding! Clear pronunciation, excellent rhythm and articulation."
            accuracyScore >= 75 -> "Good job! Clear speaking. Pay attention to missing words: ${missingWords.joinToString()}."
            accuracyScore >= 50 -> "Decent attempt. Try slowing down slightly and pronouncing each syllable carefully."
            else -> "Keep practicing! Listen to the native audio again and repeat along."
        }

        return SpeakingEvaluation(
            recognizedText = recognizedText,
            pronunciationScore = pronunciationScore,
            accuracyScore = accuracyScore,
            speedWpm = estimatedWpm,
            missingWords = missingWords,
            feedback = feedback
        )
    }

    private fun computeLevenshtein(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        speechRecognizer?.destroy()
    }

    fun shutdown() = release()
}
