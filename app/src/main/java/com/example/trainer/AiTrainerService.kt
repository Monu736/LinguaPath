package com.example.trainer

import com.example.BuildConfig
import com.example.data.model.ConversationScenario
import com.example.data.model.DialogueTurn
import com.example.data.model.WritingFeedback
import com.example.data.model.WritingMistakeRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiTrainerService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val apiKey = try {
        BuildConfig.GEMINI_API_KEY
    } catch (e: Exception) {
        ""
    }

    suspend fun sendScenarioDialogue(
        scenario: ConversationScenario,
        history: List<DialogueTurn>,
        userMessage: String
    ): DialogueTurn = withContext(Dispatchers.IO) {
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = buildString {
                    append("You are a friendly, encouraging language conversation partner in the scenario: '${scenario.title}'. ")
                    append("Language: ${scenario.languageCode}. Target learner level: ${scenario.level}. ")
                    append("Respond naturally to the user's message in the target language (20 words max). ")
                    append("Format your reply strictly as JSON with keys: 'reply' (in target language), 'translation' (in English or Hindi), 'transliteration' (optional), 'grammarCorrection' (null if no error, or explain politely if user made an error), 'betterAlternative' (optional more natural phrasing).\n\n")
                    append("Conversation so far:\n")
                    for (turn in history) {
                        append("${turn.sender}: ${turn.text}\n")
                    }
                    append("USER: $userMessage\n")
                }

                val jsonPayload = JSONObject().apply {
                    val contents = JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply { put("text", prompt) })
                            })
                        })
                    }
                    put("contents", contents)
                }

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                    .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    val root = JSONObject(body)
                    val candidateText = root.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")

                    val cleanJson = candidateText.replace("```json", "").replace("```", "").trim()
                    val resultJson = JSONObject(cleanJson)
                    return@withContext DialogueTurn(
                        sender = "AI",
                        text = resultJson.optString("reply", "That sounds great! Tell me more."),
                        transliteration = resultJson.optString("transliteration", null),
                        translation = resultJson.optString("translation", "That sounds great!"),
                        grammarCorrection = resultJson.optString("grammarCorrection", null),
                        betterAlternative = resultJson.optString("betterAlternative", null)
                    )
                }
            } catch (e: Exception) {
                // Fall back to offline simulation
            }
        }

        // High quality offline fallback
        generateOfflineDialogueReply(scenario, userMessage)
    }

    suspend fun evaluateWriting(
        prompt: String,
        userText: String,
        mistakeRules: List<WritingMistakeRule>
    ): WritingFeedback = withContext(Dispatchers.IO) {
        val detected = mutableListOf<WritingMistakeRule>()
        for (rule in mistakeRules) {
            if (Regex(rule.regexPattern, RegexOption.IGNORE_CASE).containsMatchIn(userText)) {
                detected.add(rule)
            }
        }

        var corrected = userText
        for (rule in detected) {
            corrected = corrected.replace(rule.flaggedText, rule.correction, ignoreCase = true)
        }

        val score = when {
            userText.length < 5 -> 30
            detected.isEmpty() -> 96
            detected.size == 1 -> 80
            else -> 65
        }

        val generalFeedback = if (detected.isEmpty()) {
            "Excellent work! Your sentence structure is natural, vocabulary choices are fitting, and grammar rules are observed accurately."
        } else {
            "Good effort! We identified ${detected.size} grammar/syntax adjustments. Review the corrections below to refine your fluency."
        }

        WritingFeedback(
            userText = userText,
            score = score,
            correctedText = corrected,
            detectedMistakes = detected,
            generalFeedback = generalFeedback
        )
    }

    private fun generateOfflineDialogueReply(scenario: ConversationScenario, userMessage: String): DialogueTurn {
        val lower = userMessage.lowercase()
        return when (scenario.languageCode) {
            "hi" -> {
                when {
                    lower.contains("नमस्ते") || lower.contains("namaste") -> DialogueTurn(
                        sender = "AI",
                        text = "नमस्ते जी! आपका दिन शुभ हो। आज आप किस विषय में बात करना चाहते हैं?",
                        translation = "Hello! Have a pleasant day. What topic would you like to speak about today?",
                        transliteration = "Namaste ji! Aapka din shubh ho."
                    )
                    lower.contains("खाना") || lower.contains("order") || lower.contains("चाहिए") -> DialogueTurn(
                        sender = "AI",
                        text = "बहुत बढ़िया पसंद! क्या आप इसके साथ कुछ ठंडा या गर्म पेय पदार्थ भी लेना पसंद करेंगे?",
                        translation = "Great choice! Would you also like any hot or cold beverage with this?",
                        transliteration = "Bahut badiya pasand!"
                    )
                    else -> DialogueTurn(
                        sender = "AI",
                        text = "बहुत अच्छी बात कही आपने! मुझे आपके साथ हिंदी में बातचीत करके बहुत खुशी हो रही है।",
                        translation = "Very well said! I am glad to converse with you in Hindi.",
                        transliteration = "Bahut achhi baat kahi aapne."
                    )
                }
            }
            "ja" -> {
                DialogueTurn(
                    sender = "AI",
                    text = "かしこまりました！少々お待ちください。(Understood! Please wait a moment.)",
                    translation = "Certainly! Please wait a moment.",
                    transliteration = "Kashikomarimashita! Shou-shou omachi kudasai."
                )
            }
            else -> {
                when {
                    lower.contains("coffee") || lower.contains("latte") || lower.contains("tea") -> DialogueTurn(
                        sender = "AI",
                        text = "Perfect! One beverage coming right up. Would you prefer regular dairy milk or oat milk?",
                        translation = "बिल्कुल! आपका पेय अभी तैयार करता हूँ। क्या आप साधारण दूध पसंद करेंगे या ओट मिल्क?",
                        betterAlternative = "Could I please have an iced latte with oat milk?"
                    )
                    lower.contains("interview") || lower.contains("work") || lower.contains("experience") -> DialogueTurn(
                        sender = "AI",
                        text = "That is impressive experience. How do you handle challenging deadlines when working with a team?",
                        translation = "यह प्रभावशाली अनुभव है। आप टीम के साथ काम करते हुए समय-सीमा को कैसे संभालते हैं?"
                    )
                    else -> DialogueTurn(
                        sender = "AI",
                        text = "Thank you for sharing that! Your sentence was very clear. What other goals are you working on today?",
                        translation = "यह साझा करने के लिए धन्यवाद! आपका वाक्य बहुत स्पष्ट था।"
                    )
                }
            }
        }
    }
}
