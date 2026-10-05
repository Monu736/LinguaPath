package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SupportedLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flag: String,
    val isPrimary: Boolean,
    val scriptName: String,
    val description: String
) {
    ENGLISH(
        code = "en",
        displayName = "English",
        nativeName = "English",
        flag = "🇬🇧",
        isPrimary = true,
        scriptName = "Latin",
        description = "Global language for business, travel, academics, and communication"
    ),
    HINDI(
        code = "hi",
        displayName = "Hindi",
        nativeName = "हिन्दी",
        flag = "🇮🇳",
        isPrimary = true,
        scriptName = "Devanagari (देवनागरी)",
        description = "National language of India, rich literature, Swar, Vyanjan, and Matras"
    ),
    MARATHI(
        code = "mr",
        displayName = "Marathi",
        nativeName = "मराठी",
        flag = "🇮🇳",
        isPrimary = false,
        scriptName = "Devanagari (देवनागरी)",
        description = "Language of Maharashtra, expressive phrases, formal and colloquial speech"
    ),
    JAPANESE(
        code = "ja",
        displayName = "Japanese",
        nativeName = "日本語",
        flag = "🇯🇵",
        isPrimary = false,
        scriptName = "Hiragana, Katakana & Kanji",
        description = "Side course covering Kana syllabary, Romaji, key particles, and honorifics"
    );

    companion object {
        fun fromCode(code: String): SupportedLanguage =
            entries.firstOrNull { it.code == code } ?: ENGLISH
    }
}

enum class MemoryState {
    NEW,
    LEARNING,
    FAMILIAR,
    MASTERED
}

enum class LifestyleCategory(
    val key: String,
    val title: String,
    val emoji: String,
    val tagline: String,
    val isTrending: Boolean = false,
    val accentHex: Long = 0xFF6366F1
) {
    FOOD(
        key = "FOOD",
        title = "Food & Dining",
        emoji = "🍕",
        tagline = "Street food, café orders, recipes & restaurant conversations",
        accentHex = 0xFFFF6B6B
    ),
    TRAVEL(
        key = "TRAVEL",
        title = "Travel & Adventure",
        emoji = "✈️",
        tagline = "Airports, navigation, tickets & global exploration",
        accentHex = 0xFF4D96FF
    ),
    JOB(
        key = "JOB",
        title = "Job, Office & Career",
        emoji = "💼",
        tagline = "Interviews, office talk, emails & business pitches",
        accentHex = 0xFF6C5CE7
    ),
    SCHOOL(
        key = "SCHOOL",
        title = "School & College Life",
        emoji = "🎒",
        tagline = "Classes, exams, canteen gossips & campus terms",
        accentHex = 0xFFFD79A8
    ),
    ROUTINE(
        key = "ROUTINE",
        title = "Daily Routine & Habits",
        emoji = "⏰",
        tagline = "Mornings, workouts, chores, commuting & sleep",
        accentHex = 0xFF00B894
    ),
    GAMING(
        key = "GAMING",
        title = "Gaming & Esports",
        emoji = "🎮",
        tagline = "Discord voice comms, clutch plays, battle royale & RPGs",
        isTrending = true,
        accentHex = 0xFF9B51E0
    ),
    SOCIAL_MEDIA(
        key = "SOCIAL_MEDIA",
        title = "Social Media & Slang",
        emoji = "📱",
        tagline = "Insta captions, TikTok trends, Gen-Z slang & viral memes",
        isTrending = true,
        accentHex = 0xFFFF7675
    ),
    ENTERTAINMENT(
        key = "ENTERTAINMENT",
        title = "Anime, Music & Movies",
        emoji = "🎵",
        tagline = "Anime quotes, songs, webtoons & pop culture trivia",
        isTrending = true,
        accentHex = 0xFFE84393
    ),
    SHOPPING(
        key = "SHOPPING",
        title = "Shopping & Streetwear",
        emoji = "🛍️",
        tagline = "Sneakers, bargaining discounts, styles & online orders",
        isTrending = true,
        accentHex = 0xFFF39C12
    ),
    FRIENDS(
        key = "FRIENDS",
        title = "Friends, Dating & Vibe",
        emoji = "💬",
        tagline = "Crushes, dating, jokes, cheering up & deep late chats",
        accentHex = 0xFF00CEC9
    );

    companion object {
        fun fromKey(key: String): LifestyleCategory =
            entries.firstOrNull { it.key == key } ?: FOOD
    }
}

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val userName: String = "Monu Gupta",
    val userEmail: String = "monugupta7478@gmail.com",
    val userPhone: String = "+91 98765 43210",
    val isLoggedIn: Boolean = true,
    val avatarEmoji: String = "⚡",
    val currentLanguageCode: String = "en",
    val nativeLanguage: String = "Hindi",
    val currentLevel: String = "Beginner",
    val reasons: String = "Speaking, Career, Travel, Daily communication",
    val improvementFocus: String = "Vocabulary, Speaking, Quizzes, Slang",
    val dailyGoalMinutes: Int = 15,
    val isOnboardingCompleted: Boolean = true,
    val xp: Int = 240,
    val streak: Int = 3,
    val lastActiveDate: String = "",
    val wordsLearnedCount: Int = 18,
    val lessonsCompletedCount: Int = 6,
    val overallAccuracy: Int = 88,
    val notificationsEnabled: Boolean = true,
    val hearts: Int = 5,
    val maxHearts: Int = 5,
    val gems: Int = 450,
    val streakFreezeCount: Int = 2,
    val currentLeague: String = "Ruby League",
    val leagueRank: Int = 4
)

@Entity(tableName = "quiz_level_progress")
data class QuizLevelProgress(
    @PrimaryKey val id: String, // "${languageCode}_${categoryKey}_$levelNumber"
    val languageCode: String,
    val categoryKey: String,
    val levelNumber: Int, // 1 to 100
    val stars: Int = 0, // 0 to 3
    val isUnlocked: Boolean = false,
    val highScore: Int = 0,
    val completedTimestamp: Long = 0L
)

@Entity(tableName = "language_progress")
data class LanguageProgress(
    @PrimaryKey val languageCode: String,
    val level: String = "Beginner",
    val xp: Int = 0,
    val streak: Int = 1,
    val wordsLearned: Int = 0,
    val lessonsCompleted: Int = 0,
    val accuracy: Int = 85,
    val speakingScore: Int = 80,
    val listeningScore: Int = 82,
    val readingScore: Int = 85,
    val writingScore: Int = 78,
    val weakAreas: String = "Past tense, Phrasal verbs",
    val strengths: String = "Greetings, Daily vocabulary"
)

@Entity(tableName = "vocabulary_items")
data class VocabularyItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val languageCode: String,
    val word: String,
    val transliteration: String = "",
    val meaning: String,
    val pronunciation: String,
    val exampleSentence: String,
    val exampleTranslation: String,
    val category: String,
    val difficulty: String = "Beginner",
    val memoryState: String = "NEW", // NEW, LEARNING, FAMILIAR, MASTERED
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val reviewIntervalHours: Int = 4,
    val nextReviewTimestamp: Long = 0L,
    val lastReviewedTimestamp: Long = 0L,
    val isSaved: Boolean = false
)

@Entity(tableName = "grammar_lessons")
data class GrammarLesson(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val languageCode: String,
    val topicKey: String,
    val title: String,
    val subtitle: String,
    val level: String = "Beginner",
    val explanation: String,
    val rulesJson: String,
    val examplesJson: String,
    val commonMistakesJson: String,
    val exercisesJson: String,
    val isCompleted: Boolean = false,
    val score: Int = 0
)

@Entity(tableName = "mistake_items")
data class MistakeItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val languageCode: String,
    val category: String, // Vocabulary, Grammar, Spelling, Pronunciation, Translation, Sentence formation, Listening
    val prompt: String,
    val userAnswer: String,
    val correctAnswer: String,
    val explanation: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false
)

@Entity(tableName = "daily_challenges")
data class DailyChallenge(
    @PrimaryKey val dateKey: String,
    val languageCode: String,
    val targetWords: Int = 5,
    val currentWords: Int = 0,
    val targetGrammar: Int = 5,
    val currentGrammar: Int = 0,
    val targetSpeaking: Int = 3,
    val currentSpeaking: Int = 0,
    val minAccuracy: Int = 80,
    val currentAccuracy: Int = 85,
    val isCompleted: Boolean = false,
    val rewardXp: Int = 50
)

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean = false,
    val progress: Float = 0f
)
