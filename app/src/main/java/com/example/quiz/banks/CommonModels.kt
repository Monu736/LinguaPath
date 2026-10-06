package com.example.quiz.banks

import com.example.data.model.QuizQuestion
import com.example.data.model.QuizType
import com.example.data.model.SupportedLanguage

data class LevelLessonBlueprint(
    val title: String,
    val topicDescription: String,
    val mcqQuestion: String,
    val mcqOptions: List<String>,
    val mcqAnswer: String,
    val mcqExplain: String,
    val audioText: String,
    val audioQuestion: String,
    val audioOptions: List<String>,
    val audioAnswer: String,
    val scrambleWords: List<String>,
    val targetSentence: String,
    val fillQuestion: String,
    val fillOptions: List<String>,
    val fillAnswer: String,
    val fillExplain: String,
    val tfQuestion: String,
    val tfAnswer: Boolean,
    val tfExplain: String
) {
    fun toQuestions(prefixId: String, language: SupportedLanguage): List<QuizQuestion> {
        val tfOptions = when (language) {
            SupportedLanguage.HINDI -> listOf("सच (True)", "झूठ (False)")
            SupportedLanguage.MARATHI -> listOf("खरे (True)", "खोटे (False)")
            SupportedLanguage.JAPANESE -> listOf("正しい (True)", "間違い (False)")
            SupportedLanguage.ENGLISH -> listOf("True", "False")
        }
        val tfCorrect = when (language) {
            SupportedLanguage.HINDI -> if (tfAnswer) "सच (True)" else "झूठ (False)"
            SupportedLanguage.MARATHI -> if (tfAnswer) "खरे (True)" else "खोटे (False)"
            SupportedLanguage.JAPANESE -> if (tfAnswer) "正しい (True)" else "間違い (False)"
            SupportedLanguage.ENGLISH -> if (tfAnswer) "True" else "False"
        }

        return listOf(
            QuizQuestion(
                id = "${prefixId}_1",
                type = QuizType.MULTIPLE_CHOICE,
                question = mcqQuestion,
                options = mcqOptions,
                correctAnswer = mcqAnswer,
                explanation = mcqExplain
            ),
            QuizQuestion(
                id = "${prefixId}_2",
                type = QuizType.LISTENING,
                question = audioQuestion,
                audioText = audioText,
                options = audioOptions,
                correctAnswer = audioAnswer,
                explanation = "Audio says: '$audioText'"
            ),
            QuizQuestion(
                id = "${prefixId}_3",
                type = QuizType.SENTENCE_ARRANGEMENT,
                question = when (language) {
                    SupportedLanguage.HINDI -> "सही क्रम में वाक्य बनाएँ: (Arrange the words):"
                    SupportedLanguage.MARATHI -> "योग्य क्रमाने वाक्य लावा: (Arrange the words):"
                    SupportedLanguage.JAPANESE -> "正しい順序に並べ替えてください: (Unscramble):"
                    SupportedLanguage.ENGLISH -> "Arrange the words to form a correct sentence:"
                },
                scrambledWords = scrambleWords,
                targetSentence = targetSentence,
                correctAnswer = targetSentence,
                explanation = "Target sentence: $targetSentence"
            ),
            QuizQuestion(
                id = "${prefixId}_4",
                type = QuizType.FILL_IN_BLANK,
                question = fillQuestion,
                options = fillOptions,
                correctAnswer = fillAnswer,
                explanation = fillExplain
            ),
            QuizQuestion(
                id = "${prefixId}_5",
                type = QuizType.TRUE_FALSE,
                question = tfQuestion,
                options = tfOptions,
                correctAnswer = tfCorrect,
                explanation = tfExplain
            )
        )
    }
}
