package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Achievement
import com.example.data.model.DailyChallenge
import com.example.data.model.GrammarLesson
import com.example.data.model.LanguageProgress
import com.example.data.model.MistakeItem
import com.example.data.model.QuizLevelProgress
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyItem
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET currentLanguageCode = :langCode WHERE id = 1")
    suspend fun switchLanguage(langCode: String)

    @Query("UPDATE user_profile SET xp = xp + :points WHERE id = 1")
    suspend fun addXp(points: Int)

    @Query("UPDATE user_profile SET streak = :newStreak, lastActiveDate = :dateStr WHERE id = 1")
    suspend fun updateStreak(newStreak: Int, dateStr: String)

    @Query("UPDATE user_profile SET wordsLearnedCount = wordsLearnedCount + :count WHERE id = 1")
    suspend fun incrementWordsLearned(count: Int)

    @Query("UPDATE user_profile SET lessonsCompletedCount = lessonsCompletedCount + 1 WHERE id = 1")
    suspend fun incrementLessonsCompleted()

    @Query("UPDATE user_profile SET notificationsEnabled = :enabled WHERE id = 1")
    suspend fun updateNotifications(enabled: Boolean)

    @Query("UPDATE user_profile SET userName = :name, userEmail = :email, userPhone = :phone, avatarEmoji = :avatar, isLoggedIn = 1 WHERE id = 1")
    suspend fun updateProfileDetails(name: String, email: String, phone: String, avatar: String)

    @Query("UPDATE user_profile SET hearts = MAX(0, hearts - 1) WHERE id = 1")
    suspend fun useHeart()

    @Query("UPDATE user_profile SET hearts = 5 WHERE id = 1")
    suspend fun refillHearts()

    @Query("UPDATE user_profile SET gems = MAX(0, gems - :count) WHERE id = 1")
    suspend fun spendGems(count: Int)

    @Query("UPDATE user_profile SET gems = gems + :count WHERE id = 1")
    suspend fun addGems(count: Int)

    @Query("UPDATE user_profile SET streakFreezeCount = streakFreezeCount + 1 WHERE id = 1")
    suspend fun addStreakFreeze()
}

@Dao
interface QuizLevelProgressDao {
    @Query("SELECT * FROM quiz_level_progress WHERE languageCode = :langCode AND categoryKey = :categoryKey ORDER BY levelNumber ASC")
    fun getLevelsForCategory(langCode: String, categoryKey: String): Flow<List<QuizLevelProgress>>

    @Query("SELECT * FROM quiz_level_progress WHERE languageCode = :langCode")
    fun getAllLevelProgress(langCode: String): Flow<List<QuizLevelProgress>>

    @Query("SELECT * FROM quiz_level_progress WHERE id = :id")
    suspend fun getLevelById(id: String): QuizLevelProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: QuizLevelProgress)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(levels: List<QuizLevelProgress>)

    @Query("UPDATE quiz_level_progress SET stars = :stars, highScore = :score, isUnlocked = 1, completedTimestamp = :time WHERE id = :id")
    suspend fun completeLevel(id: String, stars: Int, score: Int, time: Long)

    @Query("UPDATE quiz_level_progress SET isUnlocked = 1 WHERE id = :id")
    suspend fun unlockLevel(id: String)
}

@Dao
interface LanguageProgressDao {
    @Query("SELECT * FROM language_progress WHERE languageCode = :langCode")
    fun getProgressForLanguage(langCode: String): Flow<LanguageProgress?>

    @Query("SELECT * FROM language_progress")
    fun getAllLanguageProgress(): Flow<List<LanguageProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: LanguageProgress)

    @Query("UPDATE language_progress SET xp = xp + :points WHERE languageCode = :langCode")
    suspend fun addLanguageXp(langCode: String, points: Int)

    @Query("UPDATE language_progress SET wordsLearned = wordsLearned + :count WHERE languageCode = :langCode")
    suspend fun incrementLanguageWords(langCode: String, count: Int)

    @Query("UPDATE language_progress SET lessonsCompleted = lessonsCompleted + 1 WHERE languageCode = :langCode")
    suspend fun incrementLanguageLessons(langCode: String)
}

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabulary_items WHERE languageCode = :langCode ORDER BY word ASC")
    fun getVocabularyForLanguage(langCode: String): Flow<List<VocabularyItem>>

    @Query("SELECT * FROM vocabulary_items WHERE languageCode = :langCode AND nextReviewTimestamp <= :currentTime ORDER BY nextReviewTimestamp ASC")
    fun getWordsDueForReview(langCode: String, currentTime: Long): Flow<List<VocabularyItem>>

    @Query("SELECT * FROM vocabulary_items WHERE languageCode = :langCode AND isSaved = 1")
    fun getSavedWords(langCode: String): Flow<List<VocabularyItem>>

    @Query("SELECT * FROM vocabulary_items WHERE languageCode = :langCode AND (word LIKE '%' || :query || '%' OR meaning LIKE '%' || :query || '%' OR transliteration LIKE '%' || :query || '%')")
    suspend fun searchVocabulary(langCode: String, query: String): List<VocabularyItem>

    @Query("SELECT * FROM vocabulary_items WHERE languageCode = :langCode LIMIT 1")
    suspend fun getRandomWordOfTheDay(langCode: String): VocabularyItem?

    @Query("SELECT COUNT(*) FROM vocabulary_items WHERE languageCode = :langCode AND memoryState = 'MASTERED'")
    fun getMasteredCount(langCode: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM vocabulary_items WHERE languageCode = :langCode")
    fun getTotalCount(langCode: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<VocabularyItem>)

    @Update
    suspend fun updateVocabularyItem(item: VocabularyItem)

    @Query("UPDATE vocabulary_items SET isSaved = NOT isSaved WHERE id = :id")
    suspend fun toggleSaved(id: Int)
}

@Dao
interface GrammarDao {
    @Query("SELECT * FROM grammar_lessons WHERE languageCode = :langCode")
    fun getGrammarLessons(langCode: String): Flow<List<GrammarLesson>>

    @Query("SELECT * FROM grammar_lessons WHERE id = :id")
    suspend fun getLessonById(id: Int): GrammarLesson?

    @Query("SELECT * FROM grammar_lessons WHERE languageCode = :langCode AND (title LIKE '%' || :query || '%' OR explanation LIKE '%' || :query || '%')")
    suspend fun searchGrammar(langCode: String, query: String): List<GrammarLesson>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lessons: List<GrammarLesson>)

    @Query("UPDATE grammar_lessons SET isCompleted = 1, score = :score WHERE id = :id")
    suspend fun markLessonCompleted(id: Int, score: Int)
}

@Dao
interface MistakesDao {
    @Query("SELECT * FROM mistake_items WHERE languageCode = :langCode AND isResolved = 0 ORDER BY timestamp DESC")
    fun getActiveMistakes(langCode: String): Flow<List<MistakeItem>>

    @Query("SELECT * FROM mistake_items WHERE languageCode = :langCode ORDER BY timestamp DESC")
    fun getAllMistakes(langCode: String): Flow<List<MistakeItem>>

    @Query("SELECT COUNT(*) FROM mistake_items WHERE languageCode = :langCode AND isResolved = 0")
    fun getActiveMistakeCount(langCode: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistake(item: MistakeItem)

    @Query("UPDATE mistake_items SET isResolved = 1 WHERE id = :id")
    suspend fun resolveMistake(id: Int)

    @Query("DELETE FROM mistake_items WHERE id = :id")
    suspend fun deleteMistake(id: Int)
}

@Dao
interface DailyChallengeDao {
    @Query("SELECT * FROM daily_challenges WHERE dateKey = :dateKey AND languageCode = :langCode LIMIT 1")
    fun getTodayChallenge(dateKey: String, langCode: String): Flow<DailyChallenge?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateChallenge(challenge: DailyChallenge)

    @Query("UPDATE daily_challenges SET currentWords = currentWords + :words, currentGrammar = currentGrammar + :grammar, currentSpeaking = currentSpeaking + :speaking WHERE dateKey = :dateKey AND languageCode = :langCode")
    suspend fun incrementChallengeProgress(dateKey: String, langCode: String, words: Int, grammar: Int, speaking: Int)

    @Query("UPDATE daily_challenges SET isCompleted = 1 WHERE dateKey = :dateKey AND languageCode = :langCode")
    suspend fun completeChallenge(dateKey: String, langCode: String)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAchievements(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(achievements: List<Achievement>)

    @Query("UPDATE achievements SET isUnlocked = 1, progress = 1.0 WHERE id = :id")
    suspend fun unlockAchievement(id: String)
}
