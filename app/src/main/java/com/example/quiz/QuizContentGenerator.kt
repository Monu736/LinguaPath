package com.example.quiz

import com.example.data.model.LifestyleCategory
import com.example.data.model.QuizQuestion
import com.example.data.model.SupportedLanguage
import com.example.quiz.banks.FoodQuizBank
import com.example.quiz.banks.FriendsQuizBank
import com.example.quiz.banks.GamingQuizBank
import com.example.quiz.banks.JobQuizBank
import com.example.quiz.banks.PopCultureQuizBank
import com.example.quiz.banks.RoutineQuizBank
import com.example.quiz.banks.SchoolQuizBank
import com.example.quiz.banks.ShoppingQuizBank
import com.example.quiz.banks.SocialQuizBank
import com.example.quiz.banks.TravelQuizBank

data class CategoryQuizData(
    val category: LifestyleCategory,
    val levelNumber: Int,
    val title: String,
    val subtitle: String,
    val xpReward: Int,
    val questions: List<QuizQuestion>
)

object QuizContentGenerator {

    fun generateQuizForLevel(
        category: LifestyleCategory,
        levelNumber: Int,
        language: SupportedLanguage
    ): CategoryQuizData {
        val safeLevel = levelNumber.coerceIn(1, 100)
        val blueprint = when (category) {
            LifestyleCategory.FOOD -> FoodQuizBank.getBlueprint(safeLevel, language)
            LifestyleCategory.TRAVEL -> TravelQuizBank.getBlueprint(safeLevel, language)
            LifestyleCategory.JOB -> JobQuizBank.getBlueprint(safeLevel, language)
            LifestyleCategory.SCHOOL -> SchoolQuizBank.getBlueprint(safeLevel, language)
            LifestyleCategory.ROUTINE -> RoutineQuizBank.getBlueprint(safeLevel, language)
            LifestyleCategory.GAMING -> GamingQuizBank.getBlueprint(safeLevel, language)
            LifestyleCategory.SOCIAL_MEDIA -> SocialQuizBank.getBlueprint(safeLevel, language)
            LifestyleCategory.ENTERTAINMENT -> PopCultureQuizBank.getBlueprint(safeLevel, language)
            LifestyleCategory.SHOPPING -> ShoppingQuizBank.getBlueprint(safeLevel, language)
            LifestyleCategory.FRIENDS -> FriendsQuizBank.getBlueprint(safeLevel, language)
        }

        val questions = blueprint.toQuestions(
            prefixId = "${language.code}_${category.key}_${safeLevel}",
            language = language
        )

        return CategoryQuizData(
            category = category,
            levelNumber = safeLevel,
            title = "Level $safeLevel: ${blueprint.title}",
            subtitle = blueprint.topicDescription,
            xpReward = 30 + (safeLevel / 10) * 10,
            questions = questions
        )
    }
}
