package com.example.data.local

import com.example.data.model.Achievement
import com.example.data.model.DailyChallenge
import com.example.data.model.GrammarLesson
import com.example.data.model.LanguageProgress
import com.example.data.model.MistakeItem
import com.example.data.model.SupportedLanguage
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyItem

object InitialDataSeeder {

    suspend fun seedDatabase(db: AppDatabase) {
        // 1. Initial User Profile
        val existingProfile = db.userDao().getUserProfileOnce()
        if (existingProfile == null) {
            db.userDao().insertOrUpdateProfile(
                UserProfile(
                    id = 1,
                    userName = "Monu Gupta",
                    userEmail = "monugupta7478@gmail.com",
                    userPhone = "+91 98765 43210",
                    isLoggedIn = true,
                    avatarEmoji = "⚡",
                    currentLanguageCode = "en",
                    nativeLanguage = "Hindi",
                    currentLevel = "Beginner",
                    reasons = "Speaking, Career, Daily communication",
                    improvementFocus = "Vocabulary, Speaking, Quizzes, Slang",
                    dailyGoalMinutes = 15,
                    isOnboardingCompleted = true,
                    xp = 240,
                    streak = 4,
                    lastActiveDate = "2026-10-05",
                    wordsLearnedCount = 20,
                    lessonsCompletedCount = 6,
                    overallAccuracy = 90
                )
            )
        }

        // 2. Language Progress per language
        for (lang in SupportedLanguage.entries) {
            db.languageProgressDao().insertOrUpdateProgress(
                LanguageProgress(
                    languageCode = lang.code,
                    level = if (lang == SupportedLanguage.ENGLISH) "Elementary" else "Beginner",
                    xp = when (lang) {
                        SupportedLanguage.ENGLISH -> 140
                        SupportedLanguage.HINDI -> 110
                        SupportedLanguage.MARATHI -> 40
                        SupportedLanguage.JAPANESE -> 50
                    },
                    streak = if (lang.isPrimary) 4 else 2,
                    wordsLearned = when (lang) {
                        SupportedLanguage.ENGLISH -> 24
                        SupportedLanguage.HINDI -> 18
                        SupportedLanguage.MARATHI -> 8
                        SupportedLanguage.JAPANESE -> 10
                    },
                    lessonsCompleted = when (lang) {
                        SupportedLanguage.ENGLISH -> 6
                        SupportedLanguage.HINDI -> 4
                        SupportedLanguage.MARATHI -> 2
                        SupportedLanguage.JAPANESE -> 2
                    },
                    accuracy = 86,
                    speakingScore = 82,
                    listeningScore = 85,
                    readingScore = 88,
                    writingScore = 80,
                    weakAreas = if (lang == SupportedLanguage.ENGLISH) "Past tense, Articles" else "Noun gender agreement",
                    strengths = "Greetings, Daily objects, Numbers"
                )
            )
        }

        // 3. Seed Vocabulary
        db.vocabularyDao().insertAll(getEnglishVocabulary())
        db.vocabularyDao().insertAll(getHindiVocabulary())
        db.vocabularyDao().insertAll(getMarathiVocabulary())
        db.vocabularyDao().insertAll(getJapaneseVocabulary())

        // 4. Seed Grammar Lessons
        db.grammarDao().insertAll(getEnglishGrammarLessons())
        db.grammarDao().insertAll(getHindiGrammarLessons())
        db.grammarDao().insertAll(getMarathiGrammarLessons())
        db.grammarDao().insertAll(getJapaneseGrammarLessons())

        // 5. Seed Initial Mistakes (for Mistake Notebook "🧠 MY MISTAKES")
        db.mistakesDao().insertMistake(
            MistakeItem(
                languageCode = "en",
                category = "Grammar",
                prompt = "Fill in the blank: 'She ___ to the market yesterday.'",
                userAnswer = "goes",
                correctAnswer = "went",
                explanation = "Yesterday indicates past tense. The past tense form of 'go' is 'went'.",
                timestamp = System.currentTimeMillis() - 3600000L * 5,
                isResolved = false
            )
        )
        db.mistakesDao().insertMistake(
            MistakeItem(
                languageCode = "en",
                category = "Vocabulary",
                prompt = "What is the synonym of 'Curious'?",
                userAnswer = "Lazy",
                correctAnswer = "Inquisitive",
                explanation = "'Curious' means eager to know or learn something, similar to 'inquisitive'.",
                timestamp = System.currentTimeMillis() - 3600000L * 12,
                isResolved = false
            )
        )
        db.mistakesDao().insertMistake(
            MistakeItem(
                languageCode = "en",
                category = "Sentence formation",
                prompt = "Rearrange: 'waiting / been / I / for / have / you'",
                userAnswer = "I have you been waiting for",
                correctAnswer = "I have been waiting for you",
                explanation = "Present perfect continuous structure: Subject + have/has been + verb-ing + Object.",
                timestamp = System.currentTimeMillis() - 3600000L * 24,
                isResolved = false
            )
        )

        // 6. Seed Daily Challenge
        db.dailyChallengeDao().insertOrUpdateChallenge(
            DailyChallenge(
                dateKey = "2026-09-14",
                languageCode = "en",
                targetWords = 5,
                currentWords = 3,
                targetGrammar = 5,
                currentGrammar = 2,
                targetSpeaking = 3,
                currentSpeaking = 1,
                minAccuracy = 80,
                currentAccuracy = 86,
                isCompleted = false,
                rewardXp = 50
            )
        )

        // 7. Seed Achievements
        db.achievementDao().insertAll(
            listOf(
                Achievement("first_lesson", "First Lesson", "Completed your very first learning session", "🏆", true, 1.0f),
                Achievement("streak_7", "7-Day Streak", "Kept your learning momentum for 7 consecutive days", "🔥", false, 0.57f),
                Achievement("words_100", "100 Words Mastered", "Learned and recalled 100 vocabulary words", "📚", false, 0.24f),
                Achievement("speaking_pro", "Speaking Pro", "Achieved 90%+ pronunciation accuracy in speaking practice", "🗣️", true, 1.0f),
                Achievement("writing_master", "Writing Master", "Wrote 10 exercises with zero syntax errors", "✍️", false, 0.4f),
                Achievement("perfect_quiz", "Perfect Quiz", "Scored 100% in a comprehensive adaptive quiz", "🎯", true, 1.0f),
                Achievement("language_explorer", "Language Explorer", "Practiced in both primary and side languages", "🌎", false, 0.75f)
            )
        )

        // 8. Seed 100-Level Quiz Progress for Lifestyle Categories
        seedQuizLevels(db)
    }

    private suspend fun seedQuizLevels(db: AppDatabase) {
        val quizLevels = mutableListOf<com.example.data.model.QuizLevelProgress>()
        for (lang in com.example.data.model.SupportedLanguage.entries) {
            for (category in com.example.data.model.LifestyleCategory.entries) {
                // Generate 100 levels for each category
                for (lvl in 1..100) {
                    val id = "${lang.code}_${category.key}_$lvl"
                    // By default, level 1 is unlocked. To give an awesome first-time experience,
                    // level 1 and 2 for FOOD and GAMING have completed stars and level 3 unlocked
                    val isUnlocked = lvl == 1 || (lvl <= 3 && (category == com.example.data.model.LifestyleCategory.FOOD || category == com.example.data.model.LifestyleCategory.GAMING))
                    val stars = when {
                        lvl == 1 && (category == com.example.data.model.LifestyleCategory.FOOD || category == com.example.data.model.LifestyleCategory.GAMING) -> 3
                        lvl == 2 && (category == com.example.data.model.LifestyleCategory.FOOD || category == com.example.data.model.LifestyleCategory.GAMING) -> 2
                        else -> 0
                    }
                    val score = if (stars > 0) stars * 35 else 0
                    quizLevels.add(
                        com.example.data.model.QuizLevelProgress(
                            id = id,
                            languageCode = lang.code,
                            categoryKey = category.key,
                            levelNumber = lvl,
                            stars = stars,
                            isUnlocked = isUnlocked,
                            highScore = score,
                            completedTimestamp = if (stars > 0) System.currentTimeMillis() else 0L
                        )
                    )
                }
            }
        }
        db.quizLevelDao().insertAll(quizLevels)
    }

    private fun getEnglishVocabulary(): List<VocabularyItem> = listOf(
        VocabularyItem(
            languageCode = "en",
            word = "Curious",
            meaning = "Wanting to know or learn something new",
            pronunciation = "KYOOR-ee-uhs",
            exampleSentence = "She is curious about different world cultures.",
            exampleTranslation = "वह दुनिया की विभिन्न संस्कृतियों के बारे में जानने के लिए उत्सुक है।",
            category = "Emotions",
            difficulty = "Beginner",
            memoryState = "LEARNING",
            correctCount = 2,
            incorrectCount = 1,
            nextReviewTimestamp = System.currentTimeMillis() - 1000L,
            isSaved = true
        ),
        VocabularyItem(
            languageCode = "en",
            word = "Resilient",
            meaning = "Able to withstand or recover quickly from difficult conditions",
            pronunciation = "ri-ZIL-yuhnt",
            exampleSentence = "Children are remarkably resilient and adapt to change.",
            exampleTranslation = "बच्चे उल्लेखनीय रूप से लचीले होते हैं और बदलाव को अपना लेते हैं।",
            category = "Adjectives",
            difficulty = "Intermediate",
            memoryState = "NEW",
            nextReviewTimestamp = System.currentTimeMillis() - 2000L,
            isSaved = false
        ),
        VocabularyItem(
            languageCode = "en",
            word = "Persevere",
            meaning = "Continue in a course of action even in the face of difficulty",
            pronunciation = "pur-suh-VEER",
            exampleSentence = "If you persevere in learning English, you will succeed.",
            exampleTranslation = "यदि आप अंग्रेजी सीखने में दृढ़ बने रहेंगे, तो आप सफल होंगे।",
            category = "Common verbs",
            difficulty = "Intermediate",
            memoryState = "FAMILIAR",
            correctCount = 4,
            isSaved = true
        ),
        VocabularyItem(
            languageCode = "en",
            word = "Essential",
            meaning = "Extremely important and absolutely necessary",
            pronunciation = "ih-SEN-shuhl",
            exampleSentence = "Water and sleep are essential for good health.",
            exampleTranslation = "अच्छे स्वास्थ्य के लिए पानी और नींद बेहद आवश्यक हैं।",
            category = "Daily life",
            difficulty = "Beginner",
            memoryState = "MASTERED",
            correctCount = 6,
            isSaved = false
        ),
        VocabularyItem(
            languageCode = "en",
            word = "Gratitude",
            meaning = "The quality of being thankful and showing appreciation",
            pronunciation = "GRAT-ih-tood",
            exampleSentence = "Expressing gratitude makes everyday life happier.",
            exampleTranslation = "कृतज्ञता व्यक्त करने से रोजमर्रा की जिंदगी और खुशहाल बनती है।",
            category = "Emotions",
            difficulty = "Beginner",
            memoryState = "LEARNING",
            correctCount = 1,
            nextReviewTimestamp = System.currentTimeMillis() - 3000L,
            isSaved = true
        ),
        VocabularyItem(
            languageCode = "en",
            word = "Opportunity",
            meaning = "A set of circumstances that makes it possible to do something",
            pronunciation = "ah-pur-TOO-nuh-tee",
            exampleSentence = "This job is a wonderful opportunity to grow.",
            exampleTranslation = "यह नौकरी आगे बढ़ने का एक शानदार अवसर है।",
            category = "Work",
            difficulty = "Intermediate",
            memoryState = "FAMILIAR",
            correctCount = 3,
            isSaved = false
        ),
        VocabularyItem(
            languageCode = "en",
            word = "Negotiate",
            meaning = "Try to reach an agreement or compromise by discussion",
            pronunciation = "nih-GOH-shee-ayt",
            exampleSentence = "They were able to negotiate a better deal.",
            exampleTranslation = "वे बातचीत करके एक बेहतर सौदा तय करने में सफल रहे।",
            category = "Work",
            difficulty = "Advanced",
            memoryState = "NEW",
            nextReviewTimestamp = System.currentTimeMillis() - 4000L,
            isSaved = false
        ),
        VocabularyItem(
            languageCode = "en",
            word = "Eloquent",
            meaning = "Fluent, persuasive, and graceful in speaking or writing",
            pronunciation = "EL-uh-kwuhnt",
            exampleSentence = "The speaker gave an eloquent speech on education.",
            exampleTranslation = "वक्ता ने शिक्षा पर एक अत्यंत सुवक्ता और प्रभावशाली भाषण दिया।",
            category = "Advanced vocabulary",
            difficulty = "Advanced",
            memoryState = "NEW",
            isSaved = false
        )
    )

    private fun getHindiVocabulary(): List<VocabularyItem> = listOf(
        VocabularyItem(
            languageCode = "hi",
            word = "नमस्ते",
            transliteration = "Namaste",
            meaning = "Hello / Greetings (formal and polite)",
            pronunciation = "Nuh-muh-stay",
            exampleSentence = "नमस्ते, आप कैसे हैं?",
            exampleTranslation = "Hello, how are you?",
            category = "Daily life",
            difficulty = "Beginner",
            memoryState = "MASTERED",
            correctCount = 5,
            isSaved = true
        ),
        VocabularyItem(
            languageCode = "hi",
            word = "धन्यवाद",
            transliteration = "Dhanyavaad",
            meaning = "Thank you (polite expression of gratitude)",
            pronunciation = "Dhun-yuh-vaad",
            exampleSentence = "आपकी मदद के लिए धन्यवाद।",
            exampleTranslation = "Thank you for your help.",
            category = "Daily life",
            difficulty = "Beginner",
            memoryState = "MASTERED",
            correctCount = 4,
            isSaved = true
        ),
        VocabularyItem(
            languageCode = "hi",
            word = "दोस्त",
            transliteration = "Dost",
            meaning = "Friend (companion in life)",
            pronunciation = "Doh-st",
            exampleSentence = "राहुल मेरा सबसे अच्छा दोस्त है।",
            exampleTranslation = "Rahul is my best friend.",
            category = "Family",
            difficulty = "Beginner",
            memoryState = "FAMILIAR",
            correctCount = 3,
            isSaved = false
        ),
        VocabularyItem(
            languageCode = "hi",
            word = "खुशी",
            transliteration = "Khushi",
            meaning = "Happiness / Joy",
            pronunciation = "Khoo-shee",
            exampleSentence = "मुझे आपसे मिलकर बहुत खुशी हुई।",
            exampleTranslation = "I am very happy to meet you.",
            category = "Emotions",
            difficulty = "Beginner",
            memoryState = "LEARNING",
            correctCount = 2,
            nextReviewTimestamp = System.currentTimeMillis() - 5000L,
            isSaved = true
        ),
        VocabularyItem(
            languageCode = "hi",
            word = "सफ़र",
            transliteration = "Safar",
            meaning = "Journey / Travel",
            pronunciation = "Suh-fur",
            exampleSentence = "हमारा सफ़र बहुत यादगार और सुखद रहा।",
            exampleTranslation = "Our journey was very memorable and pleasant.",
            category = "Travel",
            difficulty = "Intermediate",
            memoryState = "NEW",
            nextReviewTimestamp = System.currentTimeMillis() - 1000L,
            isSaved = false
        ),
        VocabularyItem(
            languageCode = "hi",
            word = "किताब",
            transliteration = "Kitaab",
            meaning = "Book",
            pronunciation = "Ki-taab",
            exampleSentence = "मैं रोज़ एक नई किताब पढ़ता हूँ।",
            exampleTranslation = "I read a new book every day.",
            category = "School",
            difficulty = "Beginner",
            memoryState = "FAMILIAR",
            correctCount = 3,
            isSaved = false
        )
    )

    private fun getMarathiVocabulary(): List<VocabularyItem> = listOf(
        VocabularyItem(
            languageCode = "mr",
            word = "नमस्कार",
            transliteration = "Namaskar",
            meaning = "Greetings / Hello",
            pronunciation = "Nuh-muh-skaar",
            exampleSentence = "नमस्कार! तुम्ही कसे आहात?",
            exampleTranslation = "Hello! How are you?",
            category = "Daily life",
            difficulty = "Beginner",
            memoryState = "MASTERED",
            correctCount = 4,
            isSaved = true
        ),
        VocabularyItem(
            languageCode = "mr",
            word = "धन्यवाद",
            transliteration = "Dhanyavaad",
            meaning = "Thank you",
            pronunciation = "Dhun-yuh-vaad",
            exampleSentence = "मोठ्या मदतीबद्दल धन्यवाद!",
            exampleTranslation = "Thank you for the big help!",
            category = "Daily life",
            difficulty = "Beginner",
            memoryState = "FAMILIAR",
            correctCount = 3,
            isSaved = false
        ),
        VocabularyItem(
            languageCode = "mr",
            word = "पाणी",
            transliteration = "Paani",
            meaning = "Water",
            pronunciation = "Paa-nee",
            exampleSentence = "मला पिण्यासाठी थोडे पाणी हवे आहे.",
            exampleTranslation = "I need some water to drink.",
            category = "Food",
            difficulty = "Beginner",
            memoryState = "LEARNING",
            nextReviewTimestamp = System.currentTimeMillis() - 2000L,
            isSaved = true
        ),
        VocabularyItem(
            languageCode = "mr",
            word = "घर",
            transliteration = "Ghar",
            meaning = "Home / House",
            pronunciation = "Ghur",
            exampleSentence = "आमचे घर खूप सुंदर आणि शांत आहे.",
            exampleTranslation = "Our home is very beautiful and peaceful.",
            category = "Places",
            difficulty = "Beginner",
            memoryState = "NEW",
            isSaved = false
        ),
        VocabularyItem(
            languageCode = "mr",
            word = "जेवण",
            transliteration = "Jevan",
            meaning = "Meal / Food",
            pronunciation = "Jay-vun",
            exampleSentence = "आजचे जेवण खूप चविष्ट झाले आहे.",
            exampleTranslation = "Today's meal turned out very delicious.",
            category = "Food",
            difficulty = "Beginner",
            memoryState = "NEW",
            isSaved = true
        )
    )

    private fun getJapaneseVocabulary(): List<VocabularyItem> = listOf(
        VocabularyItem(
            languageCode = "ja",
            word = "こんにちは",
            transliteration = "Konnichiwa",
            meaning = "Hello / Good afternoon",
            pronunciation = "kohn-nee-chee-wah",
            exampleSentence = "こんにちは！お元気ですか？",
            exampleTranslation = "Hello! How are you?",
            category = "Daily life",
            difficulty = "Beginner",
            memoryState = "MASTERED",
            correctCount = 5,
            isSaved = true
        ),
        VocabularyItem(
            languageCode = "ja",
            word = "ありがとう",
            transliteration = "Arigatou",
            meaning = "Thank you",
            pronunciation = "ah-ree-gah-toh",
            exampleSentence = "どうもありがとうございます。",
            exampleTranslation = "Thank you very much.",
            category = "Daily life",
            difficulty = "Beginner",
            memoryState = "MASTERED",
            correctCount = 4,
            isSaved = true
        ),
        VocabularyItem(
            languageCode = "ja",
            word = "水 (みず)",
            transliteration = "Mizu",
            meaning = "Water",
            pronunciation = "mee-zoo",
            exampleSentence = "水をください。",
            exampleTranslation = "Please give me water.",
            category = "Food",
            difficulty = "Beginner",
            memoryState = "LEARNING",
            nextReviewTimestamp = System.currentTimeMillis() - 3000L,
            isSaved = true
        ),
        VocabularyItem(
            languageCode = "ja",
            word = "友達 (ともだち)",
            transliteration = "Tomodachi",
            meaning = "Friend",
            pronunciation = "toh-moh-dah-chee",
            exampleSentence = "彼は私の大切な友達です。",
            exampleTranslation = "He is my precious friend.",
            category = "Family",
            difficulty = "Beginner",
            memoryState = "FAMILIAR",
            correctCount = 2,
            isSaved = false
        ),
        VocabularyItem(
            languageCode = "ja",
            word = "美味しい (おいしい)",
            transliteration = "Oishii",
            meaning = "Delicious / Tasty",
            pronunciation = "oy-shee",
            exampleSentence = "このラーメンはとても美味しいです！",
            exampleTranslation = "This ramen is very delicious!",
            category = "Food",
            difficulty = "Beginner",
            memoryState = "NEW",
            isSaved = false
        )
    )

    private fun getEnglishGrammarLessons(): List<GrammarLesson> = listOf(
        GrammarLesson(
            languageCode = "en",
            topicKey = "tenses_past_simple",
            title = "Past Simple Tense",
            subtitle = "Expressing actions finished at a specific past time",
            level = "Beginner",
            explanation = "Use the Simple Past tense for actions completed in the past. Regular verbs add -ed (walk -> walked), while irregular verbs have unique forms (go -> went, eat -> ate). In negative and question forms, use 'did' + base verb.",
            rulesJson = """[
                "Affirmative: Subject + Verb(V2) -> 'She visited London last year.'",
                "Negative: Subject + did not + Base Verb(V1) -> 'She did not visit London.' (NOT 'did not visited')",
                "Question: Did + Subject + Base Verb(V1)? -> 'Did you call him?'"
            ]""",
            examplesJson = """[
                {"en": "I went to school yesterday.", "hi": "मैं कल स्कूल गया था।"},
                {"en": "She did not like the movie.", "hi": "उसे फिल्म पसंद नहीं आई।"},
                {"en": "Did you see that beautiful sunset?", "hi": "क्या तुमने वह सुंदर सूर्यास्त देखा?"}
            ]""",
            commonMistakesJson = """[
                {"wrong": "I did not went there.", "right": "I did not go there.", "reason": "After 'did/didn't', always use the base form of the verb."},
                {"wrong": "He buyed a new phone.", "right": "He bought a new phone.", "reason": "'Buy' is an irregular verb; its past form is 'bought'."}
            ]""",
            exercisesJson = """[
                {"q": "Yesterday, we ___ (watch) an interesting documentary.", "a": "watched"},
                {"q": "He didn't ___ (see) me at the station.", "a": "see"}
            ]""",
            isCompleted = true,
            score = 90
        ),
        GrammarLesson(
            languageCode = "en",
            topicKey = "subject_verb_agreement",
            title = "Subject-Verb Agreement",
            subtitle = "Matching singular & plural subjects with their verbs",
            level = "Beginner",
            explanation = "A singular subject takes a singular verb (with -s/-es in present tense), whereas a plural subject takes a plural verb without -s. Remember: 'He/She/It goes' but 'I/You/We/They go'.",
            rulesJson = """[
                "Singular: He / She / It / John runs every morning.",
                "Plural: They / We / John and Mike run every morning.",
                "First person: I run, You run (exception: I takes plural verb form in present)."
            ]""",
            examplesJson = """[
                {"en": "The teacher explains the grammar rules clearly.", "hi": "शिक्षक व्याकरण के नियमों को स्पष्ट रूप से समझाते हैं।"},
                {"en": "The students ask thoughtful questions.", "hi": "छात्र विचारशील प्रश्न पूछते हैं।"}
            ]""",
            commonMistakesJson = """[
                {"wrong": "She have two brothers.", "right": "She has two brothers.", "reason": "Third-person singular 'she' pairs with 'has', not 'have'."},
                {"wrong": "Everyone are ready.", "right": "Everyone is ready.", "reason": "Indefinite pronouns like 'everyone' and 'everybody' are grammatically singular."}
            ]""",
            exercisesJson = """[
                {"q": "My brother ___ (play) cricket on Sundays.", "a": "plays"}
            ]""",
            isCompleted = false,
            score = 0
        ),
        GrammarLesson(
            languageCode = "en",
            topicKey = "articles",
            title = "Articles (A, An, The)",
            subtitle = "Mastering definite and indefinite noun markers",
            level = "Elementary",
            explanation = "Use 'a' before consonant sounds (a book, a university [yoo sound]). Use 'an' before vowel sounds (an apple, an hour [silent h]). Use 'the' when referring to specific, previously mentioned, or unique items.",
            rulesJson = """[
                "Use 'A' before consonant sound: a car, a European city.",
                "Use 'An' before vowel sound: an umbrella, an honest person.",
                "Use 'The' for specific nouns: The sun, the book you gave me."
            ]""",
            examplesJson = """[
                {"en": "I need an hour to finish this report.", "hi": "मुझे इस रिपोर्ट को पूरा करने के लिए एक घंटे की आवश्यकता है।"},
                {"en": "He lives in a peaceful town near the river.", "hi": "वह नदी के पास एक शांत शहर में रहता है।"}
            ]""",
            commonMistakesJson = """[
                {"wrong": "He is an unique artist.", "right": "He is a unique artist.", "reason": "'Unique' begins with a consonant sound (/j/), so use 'a'."}
            ]""",
            exercisesJson = """[
                {"q": "She wants to buy ___ new car.", "a": "a"}
            ]""",
            isCompleted = false,
            score = 0
        )
    )

    private fun getHindiGrammarLessons(): List<GrammarLesson> = listOf(
        GrammarLesson(
            languageCode = "hi",
            topicKey = "hindi_gender_ling",
            title = "लिंग (Gender in Hindi)",
            subtitle = "Understanding पुल्लिंग (Masculine) and स्त्रीलिंग (Feminine)",
            level = "Beginner",
            explanation = "In Hindi, every noun has a grammatical gender: Masculine (पुल्लिंग) or Feminine (स्त्रीलिंग). The verb ending, adjectives, and postpositions change based on the gender of the noun. Masculine nouns often end in 'आ' and take 'ता है' verb endings; Feminine nouns often end in 'ई' and take 'ती है' verb endings.",
            rulesJson = """[
                "पुल्लिंग नियम: लड़का पढ़ता है (The boy reads). अच्छा लड़का (Good boy).",
                "स्त्रीलिंग नियम: लड़की पढ़ती है (The girl reads). अच्छी लड़की (Good girl).",
                "बहुवचन पुल्लिंग: लड़के पढ़ते हैं (Boys read).",
                "बहुवचन स्त्रीलिंग: लड़कियाँ पढ़ती हैं (Girls read)."
            ]""",
            examplesJson = """[
                {"hi": "लड़का पानी पीता है।", "trans": "Ladka paani peeta hai.", "en": "The boy drinks water."},
                {"hi": "लड़की चाय पीती है।", "trans": "Ladki chai peeti hai.", "en": "The girl drinks tea."}
            ]""",
            commonMistakesJson = """[
                {"wrong": "लड़की गाना गाता है।", "right": "लड़की गाना गाती है।", "reason": "Since 'लड़की' is feminine, the verb ending must be 'गाती है'."}
            ]""",
            exercisesJson = """[
                {"q": "नेहा किताब ___ (पढ़ता/पढ़ती) है।", "a": "पढ़ती"}
            ]""",
            isCompleted = true,
            score = 95
        ),
        GrammarLesson(
            languageCode = "hi",
            topicKey = "hindi_formal_informal",
            title = "औपचारिक व अनौपचारिक भाषा (आप, तुम, तू)",
            subtitle = "Politeness levels and honorific verb forms in Hindi",
            level = "Beginner",
            explanation = "Hindi has three levels of address for 'you': 'आप' (Aap - very polite, formal, used for elders, strangers, and respect), 'तुम' (Tum - informal, friendly, peers), and 'तू' (Tu - very intimate or informal, used with very close childhood friends or deity). Always prefer 'आप' for respectful communication.",
            rulesJson = """[
                "आप + verb ending 'ते/ती हैं' या 'इए': आप क्या करते हैं? / आप बैठिए।",
                "तुम + verb ending 'ते/ती हो' या 'ओ': तुम कहाँ रहते हो? / तुम खाओ।",
                "तू + verb ending 'ता/ती है': तू कैसा है?"
            ]""",
            examplesJson = """[
                {"hi": "आप कहाँ जा रहे हैं?", "trans": "Aap kahan ja rahe hain?", "en": "Where are you going? (Respectful)"},
                {"hi": "तुम क्या कर रहे हो?", "trans": "Tum kya kar rahe ho?", "en": "What are you doing? (Informal/Friendly)"}
            ]""",
            commonMistakesJson = """[
                {"wrong": "आप क्या करता है?", "right": "आप क्या करते हैं?", "reason": "With 'आप', always use honorific plural verb forms (करते हैं)."}
            ]""",
            exercisesJson = """[
                {"q": "सर, कृपया यहाँ ___ (बैठिए / बैठो)।", "a": "बैठिए"}
            ]""",
            isCompleted = false,
            score = 0
        )
    )

    private fun getMarathiGrammarLessons(): List<GrammarLesson> = listOf(
        GrammarLesson(
            languageCode = "mr",
            topicKey = "marathi_sentence_sov",
            title = "वाक्य रचना (SOV Word Order in Marathi)",
            subtitle = "Subject + Object + Verb structure and basic pronouns",
            level = "Beginner",
            explanation = "Marathi follows Subject - Object - Verb (SOV) order. The verb always appears at the end of the sentence. Essential pronouns: मी (I), आम्ही (We), तुम्ही (You - polite), तो/ती/ते (He/She/It).",
            rulesJson = """[
                "संरचना: कर्ता (Subject) + कर्म (Object) + क्रियापद (Verb)",
                "उदाहरण: मी (I) + पुस्तक (Book) + वाचतो (Read) -> 'मी पुस्तक वाचतो.'",
                "स्त्रीलिंग रूप: 'मी पुस्तक वाचते.'"
            ]""",
            examplesJson = """[
                {"mr": "मी मराठी शिकत आहे.", "trans": "Mee Marathi shikat aahe.", "en": "I am learning Marathi."},
                {"mr": "तुम्ही कुठे जात आहात?", "trans": "Tumhi kuthe jaat aahaat?", "en": "Where are you going?"}
            ]""",
            commonMistakesJson = """[
                {"wrong": "मी जात आहे मुंबई.", "right": "मी मुंबईला जात आहे.", "reason": "Verb must come at the end; postposition 'ला' marks destination."}
            ]""",
            exercisesJson = """[
                {"q": "मी चहा ___ (पीतो / जाते).", "a": "पीतो"}
            ]""",
            isCompleted = true,
            score = 90
        )
    )

    private fun getJapaneseGrammarLessons(): List<GrammarLesson> = listOf(
        GrammarLesson(
            languageCode = "ja",
            topicKey = "japanese_particles_wa_desu",
            title = "Basic Particles: は (Wa) and です (Desu)",
            subtitle = "Sentence topic marker and 'to be' polite copula",
            level = "Beginner",
            explanation = "In Japanese, particles connect words and define their grammatical role. 'は' (written 'ha' but pronounced 'wa') marks the topic of the sentence ('as for X...'). 'です' (desu) functions like 'am/is/are' in English and adds a polite tone.",
            rulesJson = """[
                "Structure: [Topic] は [Description/Noun] です。",
                "Example: 私は学生です (Watashi wa gakusei desu = I am a student).",
                "Negative: ではありません (dewa arimasen = is not)."
            ]""",
            examplesJson = """[
                {"ja": "これは本です。", "romaji": "Kore wa hon desu.", "en": "This is a book."},
                {"ja": "田中さんは先生です。", "romaji": "Tanaka-san wa sensei desu.", "en": "Mr. Tanaka is a teacher."}
            ]""",
            commonMistakesJson = """[
                {"wrong": "Pronouncing は as 'ha' when used as a particle.", "right": "Pronounce topic particle は as 'wa'.", "reason": "Historical kana spelling rule."}
            ]""",
            exercisesJson = """[
                {"q": "私 ___ 田中です。(Particle for topic)", "a": "は"}
            ]""",
            isCompleted = true,
            score = 100
        )
    )
}
