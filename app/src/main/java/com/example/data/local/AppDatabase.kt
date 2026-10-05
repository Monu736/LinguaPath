package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Achievement
import com.example.data.model.DailyChallenge
import com.example.data.model.GrammarLesson
import com.example.data.model.LanguageProgress
import com.example.data.model.MistakeItem
import com.example.data.model.QuizLevelProgress
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        QuizLevelProgress::class,
        LanguageProgress::class,
        VocabularyItem::class,
        GrammarLesson::class,
        MistakeItem::class,
        DailyChallenge::class,
        Achievement::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun quizLevelDao(): QuizLevelProgressDao
    abstract fun languageProgressDao(): LanguageProgressDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun grammarDao(): GrammarDao
    abstract fun mistakesDao(): MistakesDao
    abstract fun dailyChallengeDao(): DailyChallengeDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return getDatabase(context, CoroutineScope(Dispatchers.IO))
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "language_trainer.db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            scope.launch(Dispatchers.IO) {
                                InitialDataSeeder.seedDatabase(database)
                            }
                        }
                    }

                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                        super.onDestructiveMigration(db)
                        INSTANCE?.let { database ->
                            scope.launch(Dispatchers.IO) {
                                InitialDataSeeder.seedDatabase(database)
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
