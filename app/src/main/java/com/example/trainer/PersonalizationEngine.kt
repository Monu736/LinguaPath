package com.example.trainer

import com.example.data.model.ClickableWordAnnotation
import com.example.data.model.ConversationScenario
import com.example.data.model.DialogueTurn
import com.example.data.model.ListeningExercise
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizType
import com.example.data.model.ReadingPassage
import com.example.data.model.SpeakingPrompt
import com.example.data.model.VocabularyItem
import com.example.data.model.WritingExercise
import com.example.data.model.WritingMistakeRule

object PersonalizationEngine {

    fun generateDailyPlan(languageCode: String, wordsDueCount: Int): List<String> {
        val reviewCount = if (wordsDueCount > 0) wordsDueCount.coerceAtMost(10) else 5
        return listOf(
            "🧠 Review $reviewCount memory-spaced words",
            "📚 Learn 5 new essential vocabulary cards",
            "✏️ Complete targeted grammar workout",
            "🎯 Solve 8-question adaptive mastery quiz",
            "🗣️ Speak 3 sentences with pronunciation feedback"
        )
    }

    fun getAdaptiveQuizzes(languageCode: String): List<QuizQuestion> {
        return when (languageCode) {
            "hi" -> getHindiQuizzes()
            "mr" -> getMarathiQuizzes()
            "ja" -> getJapaneseQuizzes()
            else -> getEnglishQuizzes()
        }
    }

    private fun getEnglishQuizzes(): List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "en_q1",
            type = QuizType.MULTIPLE_CHOICE,
            question = "What does the word 'Eloquent' mean?",
            options = listOf("Fluent and persuasive in speech", "Lazy and indifferent", "Extremely loud", "Complicated and difficult"),
            correctAnswer = "Fluent and persuasive in speech",
            explanation = "'Eloquent' means articulate, graceful, and expressive in spoken or written language."
        ),
        QuizQuestion(
            id = "en_q2",
            type = QuizType.FILL_IN_BLANK,
            question = "I ___ going to the library right now.",
            options = listOf("am", "is", "are", "were"),
            correctAnswer = "am",
            explanation = "First-person singular pronoun 'I' takes the auxiliary verb 'am' in present continuous tense."
        ),
        QuizQuestion(
            id = "en_q3",
            type = QuizType.SENTENCE_ARRANGEMENT,
            question = "Rearrange the words into a grammatically correct sentence:",
            scrambledWords = listOf("to", "I", "school", "going", "am"),
            targetSentence = "I am going to school",
            correctAnswer = "I am going to school",
            explanation = "Standard English SVO order: Subject (I) + Aux (am) + Verb (going) + Prepositional phrase (to school)."
        ),
        QuizQuestion(
            id = "en_q4",
            type = QuizType.WORD_MATCHING,
            question = "Match English words with their Hindi meanings:",
            correctPairs = mapOf(
                "Curious" to "उत्सुक",
                "Gratitude" to "कृतज्ञता",
                "Essential" to "आवश्यक",
                "Resilient" to "लचीला / सहनशील"
            ),
            explanation = "Pairing these builds bilingual associative retention."
        ),
        QuizQuestion(
            id = "en_q5",
            type = QuizType.TRANSLATION,
            question = "Translate into English: 'मुझे पानी चाहिए।'",
            options = listOf("I want water.", "I am drinking water.", "He wants water.", "Water is here."),
            correctAnswer = "I want water.",
            explanation = "'मुझे पानी चाहिए' translates directly to 'I want water' or 'I need water'."
        ),
        QuizQuestion(
            id = "en_q6",
            type = QuizType.SPELLING,
            question = "Type the correct spelling for: /ri-ZIL-yuhnt/ (Able to recover quickly)",
            correctAnswer = "Resilient",
            explanation = "R-E-S-I-L-I-E-N-T. Notice the 's' making a /z/ sound."
        ),
        QuizQuestion(
            id = "en_q7",
            type = QuizType.TRUE_FALSE,
            question = "True or False: 'He didn't went to the concert yesterday' is grammatically correct.",
            options = listOf("True", "False"),
            correctAnswer = "False",
            explanation = "False! After 'didn't', the verb must be in its base form: 'He didn't go'."
        ),
        QuizQuestion(
            id = "en_q8",
            type = QuizType.LISTENING,
            question = "Listen and identify the sentence you heard:",
            audioText = "She has a beautiful smile and a warm heart.",
            options = listOf(
                "She has a beautiful smile and a warm heart.",
                "She had a beautiful smile and a cold heart.",
                "She has a wonderful home and warm heart.",
                "He has a beautiful smile and a warm heart."
            ),
            correctAnswer = "She has a beautiful smile and a warm heart.",
            explanation = "Attentive listening distinguishes subtle phonemes like 'has' vs 'had'."
        )
    )

    private fun getHindiQuizzes(): List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "hi_q1",
            type = QuizType.MULTIPLE_CHOICE,
            question = "'Apple' का हिंदी अर्थ क्या है?",
            options = listOf("केला", "सेब", "आम", "संतरा"),
            correctAnswer = "सेब",
            explanation = "Apple का हिंदी अर्थ सेब है।"
        ),
        QuizQuestion(
            id = "hi_q2",
            type = QuizType.FILL_IN_BLANK,
            question = "नेहा रोज़ सुबह व्यायाम ___ है।",
            options = listOf("करती", "करता", "करते", "करूँ"),
            correctAnswer = "करती",
            explanation = "चूँकि 'नेहा' स्त्रीलिंग एकवचन है, क्रिया 'करती है' होगी।"
        ),
        QuizQuestion(
            id = "hi_q3",
            type = QuizType.SENTENCE_ARRANGEMENT,
            question = "सही क्रम में वाक्य बनाएँ:",
            scrambledWords = listOf("हूँ", "किताब", "मैं", "पढ़ता"),
            targetSentence = "मैं किताब पढ़ता हूँ",
            correctAnswer = "मैं किताब पढ़ता हूँ",
            explanation = "हिंदी की वाक्य रचना कर्ता-कर्म-क्रिया (SOV) होती है।"
        ),
        QuizQuestion(
            id = "hi_q4",
            type = QuizType.WORD_MATCHING,
            question = "शब्दों का उनके अर्थ से मिलान करें:",
            correctPairs = mapOf(
                "नमस्ते" to "Greetings / Hello",
                "धन्यवाद" to "Thank you",
                "दोस्त" to "Friend",
                "खुशी" to "Happiness"
            ),
            explanation = "ये दैनिक जीवन के अत्यंत महत्वपूर्ण शब्द हैं।"
        ),
        QuizQuestion(
            id = "hi_q5",
            type = QuizType.TRUE_FALSE,
            question = "'आप क्या करता है?' व्याकरण के अनुसार सही वाक्य है?",
            options = listOf("सत्य", "असत्य"),
            correctAnswer = "असत्य",
            explanation = "असत्य! 'आप' के साथ आदरसूचक बहुवचन 'करते हैं' का प्रयोग होता है: 'आप क्या करते हैं?'"
        )
    )

    private fun getMarathiQuizzes(): List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "mr_q1",
            type = QuizType.MULTIPLE_CHOICE,
            question = "मराठीत 'Thank you' ला काय म्हणतात?",
            options = listOf("धन्यवाद", "नमस्ते", "कृपया", "स्वागत"),
            correctAnswer = "धन्यवाद",
            explanation = "मराठीत आभार मानण्यासाठी 'धन्यवाद' म्हणतात."
        ),
        QuizQuestion(
            id = "mr_q2",
            type = QuizType.FILL_IN_BLANK,
            question = "मी दररोज मराठी ___ आहे.",
            options = listOf("शिकत", "जात", "खात", "झोपत"),
            correctAnswer = "शिकत",
            explanation = "'शिकत आहे' म्हणजे 'learning'."
        ),
        QuizQuestion(
            id = "mr_q3",
            type = QuizType.SENTENCE_ARRANGEMENT,
            question = "वाक्य योग्य क्रमाने लावा:",
            scrambledWords = listOf("आहात?", "कसे", "तुम्ही"),
            targetSentence = "तुम्ही कसे आहात?",
            correctAnswer = "तुम्ही कसे आहात?",
            explanation = "मराठीत आदरपूर्वक विचारण्यासाठी 'तुम्ही कसे आहात?' म्हणतात."
        )
    )

    private fun getJapaneseQuizzes(): List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "ja_q1",
            type = QuizType.MULTIPLE_CHOICE,
            question = "What does 'こんにちは' (Konnichiwa) mean?",
            options = listOf("Hello / Good day", "Goodbye", "Thank you", "Excuse me"),
            correctAnswer = "Hello / Good day",
            explanation = "'こんにちは' is the standard midday greeting in Japanese."
        ),
        QuizQuestion(
            id = "ja_q2",
            type = QuizType.FILL_IN_BLANK,
            question = "私 ___ 田中です。(Select the topic particle)",
            options = listOf("は (wa)", "を (o)", "に (ni)", "で (de)"),
            correctAnswer = "は (wa)",
            explanation = "'は' marks '私' (I) as the topic of the sentence."
        ),
        QuizQuestion(
            id = "ja_q3",
            type = QuizType.WORD_MATCHING,
            question = "Match Japanese words with English meanings:",
            correctPairs = mapOf(
                "水 (みず)" to "Water",
                "ありがとう" to "Thank you",
                "友達 (ともだち)" to "Friend",
                "美味しい" to "Delicious"
            ),
            explanation = "Core survival vocabulary in Japanese."
        )
    )

    fun getSpeakingPrompts(languageCode: String): List<SpeakingPrompt> {
        return when (languageCode) {
            "hi" -> listOf(
                SpeakingPrompt("hi_sp1", "hi", "नमस्ते, आप कैसे हैं?", "Namaste, aap kaise hain?", "Hello, how are you?", listOf("नमस्ते", "आप", "कैसे", "हैं"), "Maintain gentle intonation on 'Aap'."),
                SpeakingPrompt("hi_sp2", "hi", "मुझे हिंदी सीखना बहुत पसंद है।", "Mujhe Hindi seekhna bahut pasand hai.", "I really like learning Hindi.", listOf("मुझे", "हिंदी", "सीखना", "पसंद"), "Enunciate 'seekhna' clearly."),
                SpeakingPrompt("hi_sp3", "hi", "क्या आप मुझे रास्ता बता सकते हैं?", "Kya aap mujhe raasta bata sakte hain?", "Could you please show me the way?", listOf("रास्ता", "सकते", "हैं"), "Clear polite request tone.")
            )
            "mr" -> listOf(
                SpeakingPrompt("mr_sp1", "mr", "नमस्कार! तुम्ही कसे आहात?", "Namaskar! Tumhi kase aahat?", "Hello! How are you?", listOf("नमस्कार", "तुम्ही", "कसे"), "Pronounce 'Namaskar' crisply."),
                SpeakingPrompt("mr_sp2", "mr", "मला थोडे पाणी हवे आहे.", "Mala thode paani have aahe.", "I need some water.", listOf("मला", "पाणी", "हवे"), "Keep the stress on 'Paani'.")
            )
            "ja" -> listOf(
                SpeakingPrompt("ja_sp1", "ja", "こんにちは！お元気ですか？", "Konnichiwa! Ogenki desu ka?", "Hello! How are you?", listOf("こんにちは", "お元気"), "Drop the pitch at 'desu ka'."),
                SpeakingPrompt("ja_sp2", "ja", "これをください。", "Kore o kudasai.", "Please give me this one.", listOf("これ", "ください"), "Pronounce the particle 'o' smoothly.")
            )
            else -> listOf(
                SpeakingPrompt("en_sp1", "en", "I would like a cup of hot coffee.", null, "मुझे एक कप गर्म कॉफी चाहिए।", listOf("would", "like", "hot", "coffee"), "Pronounce 'would like' softly without pausing between words."),
                SpeakingPrompt("en_sp2", "en", "Could you please tell me how to get to the museum?", null, "क्या आप मुझे बता सकते हैं कि संग्रहालय कैसे पहुँचना है?", listOf("Could", "please", "museum"), "Stress the second syllable in 'mu-SE-um'."),
                SpeakingPrompt("en_sp3", "en", "Practicing every single day makes learning effortless.", null, "हर दिन अभ्यास करने से सीखना आसान हो जाता है।", listOf("Practicing", "effortless"), "Ensure clear /f/ and /s/ sounds in 'effortless'.")
            )
        }
    }

    fun getConversationScenarios(languageCode: String): List<ConversationScenario> {
        return when (languageCode) {
            "hi" -> listOf(
                ConversationScenario(
                    id = "hi_cs1",
                    languageCode = "hi",
                    title = "अपना परिचय देना (Introducing Yourself)",
                    description = "Meet a new acquaintance at a seminar and exchange polite greetings.",
                    iconEmoji = "🤝",
                    level = "Beginner",
                    initialAiMessage = "नमस्ते! मेरा नाम राहुल है। आपसे मिलकर बहुत अच्छा लगा। आपका नाम क्या है?",
                    initialAiTranslation = "Hello! My name is Rahul. Very pleased to meet you. What is your name?",
                    initialAiTransliteration = "Namaste! Mera naam Rahul hai. Aapka naam kya hai?",
                    sampleUserReplies = listOf("नमस्ते! मेरा नाम अमन है।", "नमस्ते राहुल जी, मैं यहाँ पहली बार आया हूँ।")
                ),
                ConversationScenario(
                    id = "hi_cs2",
                    languageCode = "hi",
                    title = "रेस्टोरेंट में खाना ऑर्डर करना (Ordering Food)",
                    description = "Order traditional dishes at a popular Indian dining restaurant.",
                    iconEmoji = "🍲",
                    level = "Beginner",
                    initialAiMessage = "नमस्ते जी! आपका स्वागत है। आप आज क्या खाना पसंद करेंगे?",
                    initialAiTranslation = "Greetings! Welcome. What would you like to eat today?",
                    initialAiTransliteration = "Namaste ji! Aapka swaagat hai. Aap aaj kya khana pasand karenge?",
                    sampleUserReplies = listOf("कृपया मेनू दिखाइए।", "मुझे पनीर टिक्का और रोटी चाहिए।")
                )
            )
            "ja" -> listOf(
                ConversationScenario(
                    id = "ja_cs1",
                    languageCode = "ja",
                    title = "レストランでの注文 (Restaurant Order)",
                    description = "Order food and ask for recommendations at a Tokyo café.",
                    iconEmoji = "🍜",
                    level = "Beginner",
                    initialAiMessage = "いらっしゃいませ！何名様ですか？",
                    initialAiTranslation = "Welcome! How many people in your party?",
                    initialAiTransliteration = "Irasshaimase! Nanmei-sama desu ka?",
                    sampleUserReplies = listOf("一人です (Hitori desu - Just one)", "メニューをください (Menu o kudasai)")
                )
            )
            else -> listOf(
                ConversationScenario(
                    id = "en_cs1",
                    languageCode = "en",
                    title = "Ordering at a Café",
                    description = "Order coffee and a snack while chatting casually with the barista.",
                    iconEmoji = "☕",
                    level = "Beginner",
                    initialAiMessage = "Good morning! Welcome to the Daily Grind Café. What can I get started for you today?",
                    initialAiTranslation = "सुप्रभात! डेली ग्राइंड कैफे में आपका स्वागत है। आज मैं आपके लिए क्या तैयार करूँ?",
                    sampleUserReplies = listOf("I'd like an iced latte with oat milk, please.", "What kind of pastries do you have fresh today?")
                ),
                ConversationScenario(
                    id = "en_cs2",
                    languageCode = "en",
                    title = "Job Interview Practice",
                    description = "Practice answering questions about your background and strengths professionally.",
                    iconEmoji = "💼",
                    level = "Intermediate",
                    initialAiMessage = "Hello! Thanks for coming in today. Could you start by telling me a little about yourself?",
                    initialAiTranslation = "नमस्ते! आज यहाँ आने के लिए धन्यवाद। क्या आप अपने बारे में कुछ बता सकते हैं?",
                    sampleUserReplies = listOf("Certainly! I have been working in software design for three years.", "Thank you for having me. I'm passionate about creative problem-solving.")
                ),
                ConversationScenario(
                    id = "en_cs3",
                    languageCode = "en",
                    title = "Asking for Directions",
                    description = "Navigate a busy city center and ask a friendly local for directions.",
                    iconEmoji = "🗺️",
                    level = "Beginner",
                    initialAiMessage = "Excuse me, you look a bit lost! Are you looking for the Central Station or the Art Gallery?",
                    initialAiTranslation = "माफ़ कीजिए, आप थोड़े भटके हुए लग रहे हैं! क्या आप सेंट्रल स्टेशन ढूँढ रहे हैं?",
                    sampleUserReplies = listOf("Yes, I'm trying to find the nearest subway station.", "Could you point me towards the main market?")
                )
            )
        }
    }

    fun getReadingPassages(languageCode: String): List<ReadingPassage> {
        return when (languageCode) {
            "hi" -> listOf(
                ReadingPassage(
                    id = "hi_rp1",
                    languageCode = "hi",
                    level = "Beginner",
                    title = "सुबह की सैर (Morning Walk)",
                    content = "सुबह की ताज़ा हवा स्वास्थ्य के लिए बहुत लाभदायक होती है। रोहन रोज़ बगीचे में जाता है। वहाँ सुंदर फूल खिलते हैं और पक्षी चहचहाते हैं। वह अपने मित्रों के साथ थोड़ा दौड़ता है।",
                    translation = "The fresh morning air is very beneficial for health. Rohan goes to the garden every day. Beautiful flowers bloom there and birds chirp.",
                    wordAnnotations = mapOf(
                        "लाभदायक" to ClickableWordAnnotation("लाभदायक", "Beneficial / Helpful", "Laabhdaayak", "Laabh-daa-yuk", "Beneficial", "यह व्यायाम स्वास्थ्य के लिए लाभदायक है।"),
                        "बगीचे" to ClickableWordAnnotation("बगीचे", "Garden / Park", "Bageeche", "Buh-gee-chay", "Garden", "बच्चे बगीचे में खेल रहे हैं।")
                    ),
                    comprehensionQuestions = listOf(
                        QuizQuestion("hi_rp1_q1", QuizType.MULTIPLE_CHOICE, "रोहन रोज़ कहाँ जाता है?", options = listOf("बगीचे में", "दुकान पर", "स्टेशन पर"), correctAnswer = "बगीचे में")
                    )
                )
            )
            else -> listOf(
                ReadingPassage(
                    id = "en_rp1",
                    languageCode = "en",
                    level = "Beginner",
                    title = "The Power of Curiosity",
                    content = "Curiosity is the engine of human progress. When people ask thoughtful questions and explore unfamiliar subjects, they build new neural pathways. Children demonstrate natural curiosity by observing insects, rocks, and stars with pure wonder. Cultivating lifelong curiosity keeps our minds youthful and resilient.",
                    translation = "जिज्ञासा मानव प्रगति का इंजन है। जब लोग विचारशील प्रश्न पूछते हैं और अपरिचित विषयों की खोज करते हैं, तो वे नए तंत्रिका पथ बनाते हैं।",
                    wordAnnotations = mapOf(
                        "Curiosity" to ClickableWordAnnotation("Curiosity", "Eagerness to learn", null, "kyoor-ee-OSS-ih-tee", "जिज्ञासा", "Her curiosity led to great scientific discoveries."),
                        "Resilient" to ClickableWordAnnotation("Resilient", "Able to bounce back quickly", null, "ri-ZIL-yuhnt", "लचीला / सहनशील", "Healthy habits make your mind resilient under stress.")
                    ),
                    comprehensionQuestions = listOf(
                        QuizQuestion("en_rp1_q1", QuizType.MULTIPLE_CHOICE, "According to the passage, what is the engine of human progress?", options = listOf("Curiosity", "Wealth", "Silence", "Speed"), correctAnswer = "Curiosity")
                    )
                )
            )
        }
    }

    fun getListeningExercises(languageCode: String): List<ListeningExercise> {
        return listOf(
            ListeningExercise(
                id = "list_1",
                languageCode = "en",
                level = 1,
                speechText = "Good morning! Welcome to the English language training studio.",
                speedMultiplier = 0.7f,
                transcript = "Good morning! Welcome to the English language training studio.",
                translation = "सुप्रभात! अंग्रेजी भाषा प्रशिक्षण स्टूडियो में आपका स्वागत है।",
                question = QuizQuestion(
                    id = "lq1",
                    type = QuizType.MULTIPLE_CHOICE,
                    question = "Where does the speaker welcome you to?",
                    options = listOf("Language training studio", "Coffee shop", "Airport terminal", "Railway platform"),
                    correctAnswer = "Language training studio"
                )
            ),
            ListeningExercise(
                id = "list_2",
                languageCode = "en",
                level = 2,
                speechText = "Regular practice for just fifteen minutes a day yields lasting memory retention.",
                speedMultiplier = 1.0f,
                transcript = "Regular practice for just fifteen minutes a day yields lasting memory retention.",
                translation = "प्रतिदिन केवल पंद्रह मिनट का नियमित अभ्यास स्थायी स्मृति प्रतिधारण प्रदान करता है।",
                question = QuizQuestion(
                    id = "lq2",
                    type = QuizType.MULTIPLE_CHOICE,
                    question = "How many minutes of daily practice were recommended?",
                    options = listOf("15 minutes", "45 minutes", "60 minutes", "5 minutes"),
                    correctAnswer = "15 minutes"
                )
            )
        )
    }

    fun getWritingExercises(languageCode: String): List<WritingExercise> {
        return listOf(
            WritingExercise(
                id = "w1",
                languageCode = "en",
                prompt = "Describe what you usually do on Saturday morning in 1-2 complete sentences.",
                instruction = "Pay close attention to present simple subject-verb agreement (e.g. 'I wake up', 'He wakes up').",
                starterText = "On Saturday morning, I ",
                sampleAnswer = "On Saturday morning, I wake up early and drink a warm cup of tea with my family.",
                commonMistakeChecks = listOf(
                    WritingMistakeRule("\\bI goes\\b", "I goes", "I go", "With first-person pronoun 'I', use base verb 'go' without -es."),
                    WritingMistakeRule("\\bhe go\\b", "he go", "he goes", "With third-person singular 'he', add -es to form 'goes'."),
                    WritingMistakeRule("\\bdid went\\b", "did went", "did go", "After 'did', always use base verb form 'go'.")
                )
            ),
            WritingExercise(
                id = "w2",
                languageCode = "en",
                prompt = "Translate into English: 'कल मैंने एक बहुत अच्छी किताब पढ़ी।'",
                instruction = "Use past tense form of 'read'. Remember that past tense of read is spelled 'read' but pronounced 'red'.",
                starterText = "Yesterday, I ",
                sampleAnswer = "Yesterday, I read a very good book.",
                commonMistakeChecks = listOf(
                    WritingMistakeRule("\\breaded\\b", "readed", "read", "'Read' is an irregular verb. Its past tense form is 'read'."),
                    WritingMistakeRule("\\bhave readed\\b", "have readed", "have read", "Past participle of read is 'read'.")
                )
            )
        )
    }
}
