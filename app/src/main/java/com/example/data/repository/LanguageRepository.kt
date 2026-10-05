package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.Achievement
import com.example.data.model.DailyChallenge
import com.example.data.model.GrammarLesson
import com.example.data.model.LanguageProgress
import com.example.data.model.MistakeItem
import com.example.data.model.QuizLevelProgress
import com.example.data.model.SupportedLanguage
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyItem
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LanguageRepository(private val db: AppDatabase) {

    val userProfile: Flow<UserProfile?> = db.userDao().getUserProfile()

    fun getLanguageProgress(langCode: String): Flow<LanguageProgress?> =
        db.languageProgressDao().getProgressForLanguage(langCode)

    val allProgress: Flow<List<LanguageProgress>> =
        db.languageProgressDao().getAllLanguageProgress()

    fun getVocabulary(langCode: String): Flow<List<VocabularyItem>> =
        db.vocabularyDao().getVocabularyForLanguage(langCode)

    fun getWordsDueForReview(langCode: String): Flow<List<VocabularyItem>> =
        db.vocabularyDao().getWordsDueForReview(langCode, System.currentTimeMillis() + 60000L)

    fun getSavedWords(langCode: String): Flow<List<VocabularyItem>> =
        db.vocabularyDao().getSavedWords(langCode)

    fun getGrammarLessons(langCode: String): Flow<List<GrammarLesson>> =
        db.grammarDao().getGrammarLessons(langCode)

    fun getActiveMistakes(langCode: String): Flow<List<MistakeItem>> =
        db.mistakesDao().getActiveMistakes(langCode)

    fun getAllMistakes(langCode: String): Flow<List<MistakeItem>> =
        db.mistakesDao().getAllMistakes(langCode)

    fun getActiveMistakeCount(langCode: String): Flow<Int> =
        db.mistakesDao().getActiveMistakeCount(langCode)

    fun getTodayChallenge(langCode: String): Flow<DailyChallenge?> {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return db.dailyChallengeDao().getTodayChallenge(todayStr, langCode)
    }

    val achievements: Flow<List<Achievement>> =
        db.achievementDao().getAchievements()

    fun getLevelsForCategory(langCode: String, categoryKey: String): Flow<List<QuizLevelProgress>> =
        db.quizLevelDao().getLevelsForCategory(langCode, categoryKey)

    fun getAllLevelProgress(langCode: String): Flow<List<QuizLevelProgress>> =
        db.quizLevelDao().getAllLevelProgress(langCode)

    suspend fun switchLanguage(langCode: String) {
        db.userDao().switchLanguage(langCode)
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        db.userDao().insertOrUpdateProfile(profile)
    }

    suspend fun updateProfileDetails(name: String, email: String, phone: String, avatar: String) {
        db.userDao().updateProfileDetails(name, email, phone, avatar)
    }

    suspend fun useHeart() {
        db.userDao().useHeart()
    }

    suspend fun refillHearts() {
        db.userDao().refillHearts()
    }

    suspend fun buyRefillHearts() {
        db.userDao().spendGems(100)
        db.userDao().refillHearts()
    }

    suspend fun buyStreakFreeze() {
        db.userDao().spendGems(200)
        db.userDao().addStreakFreeze()
    }

    suspend fun addGems(count: Int) {
        db.userDao().addGems(count)
    }

    suspend fun addXp(langCode: String, points: Int) {
        db.userDao().addXp(points)
        db.languageProgressDao().addLanguageXp(langCode, points)
    }

    suspend fun completeLesson(langCode: String, lessonId: Int, score: Int) {
        db.grammarDao().markLessonCompleted(lessonId, score)
        db.userDao().incrementLessonsCompleted()
        db.languageProgressDao().incrementLanguageLessons(langCode)
        addXp(langCode, 25)
    }

    suspend fun completeQuizLevel(
        langCode: String,
        categoryKey: String,
        levelNumber: Int,
        stars: Int,
        score: Int
    ) {
        val currentId = "${langCode}_${categoryKey}_$levelNumber"
        val existing = db.quizLevelDao().getLevelById(currentId)
        val bestStars = maxOf(stars, existing?.stars ?: 0)
        val bestScore = maxOf(score, existing?.highScore ?: 0)

        db.quizLevelDao().completeLevel(currentId, bestStars, bestScore, System.currentTimeMillis())

        // Unlock next level if at least 1 star scored
        if (stars > 0 && levelNumber < 100) {
            val nextId = "${langCode}_${categoryKey}_${levelNumber + 1}"
            db.quizLevelDao().unlockLevel(nextId)
        }

        val xpGained = 20 + stars * 10
        addXp(langCode, xpGained)
        db.userDao().incrementLessonsCompleted()
        db.languageProgressDao().incrementLanguageLessons(langCode)
        db.languageProgressDao().incrementLanguageWords(langCode, 3)
    }

    suspend fun toggleSavedWord(id: Int) {
        db.vocabularyDao().toggleSaved(id)
    }

    suspend fun recordMistake(
        langCode: String,
        category: String,
        prompt: String,
        userAnswer: String,
        correctAnswer: String,
        explanation: String
    ) {
        val item = MistakeItem(
            languageCode = langCode,
            category = category,
            prompt = prompt,
            userAnswer = userAnswer,
            correctAnswer = correctAnswer,
            explanation = explanation,
            timestamp = System.currentTimeMillis(),
            isResolved = false
        )
        db.mistakesDao().insertMistake(item)
    }

    suspend fun resolveMistake(id: Int) {
        db.mistakesDao().resolveMistake(id)
        db.userDao().addXp(10)
    }

    suspend fun updateVocabularySpacedRepetition(
        item: VocabularyItem,
        isCorrect: Boolean,
        confidenceLevel: Int
    ) {
        val now = System.currentTimeMillis()
        val newCorrectCount = if (isCorrect) item.correctCount + 1 else item.correctCount
        val newIncorrectCount = if (!isCorrect) item.incorrectCount + 1 else item.incorrectCount

        val (newState, intervalHours) = when {
            !isCorrect -> "LEARNING" to 2
            confidenceLevel == 3 || newCorrectCount >= 5 -> "MASTERED" to 168
            confidenceLevel == 2 || newCorrectCount >= 3 -> "FAMILIAR" to 48
            else -> "LEARNING" to 12
        }

        val nextReview = now + (intervalHours * 3600L * 1000L)
        val updated = item.copy(
            memoryState = newState,
            correctCount = newCorrectCount,
            incorrectCount = newIncorrectCount,
            reviewIntervalHours = intervalHours,
            lastReviewedTimestamp = now,
            nextReviewTimestamp = nextReview
        )

        db.vocabularyDao().updateVocabularyItem(updated)
        if (isCorrect) {
            db.userDao().addXp(5)
            db.languageProgressDao().addLanguageXp(item.languageCode, 5)
        }
    }

    suspend fun searchAll(langCode: String, query: String): Pair<List<VocabularyItem>, List<GrammarLesson>> {
        val words = db.vocabularyDao().searchVocabulary(langCode, query)
        val grammar = db.grammarDao().searchGrammar(langCode, query)
        return words to grammar
    }
}
