package com.example.data.model

enum class QuizType {
    MULTIPLE_CHOICE,
    FILL_IN_BLANK,
    WORD_MATCHING,
    SENTENCE_ARRANGEMENT,
    TRANSLATION,
    SPELLING,
    LISTENING,
    TRUE_FALSE,
    SPEED_QUIZ
}

data class QuizQuestion(
    val id: String,
    val type: QuizType,
    val question: String,
    val subtitle: String? = null,
    val audioText: String? = null,
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val correctPairs: Map<String, String> = emptyMap(), // For word matching
    val scrambledWords: List<String> = emptyList(), // For sentence arrangement
    val targetSentence: String = "",
    val explanation: String = "",
    val difficulty: String = "Beginner"
)

data class SpeakingPrompt(
    val id: String,
    val languageCode: String,
    val sentence: String,
    val transliteration: String? = null,
    val translation: String,
    val targetWords: List<String>,
    val tips: String
)

data class SpeakingEvaluation(
    val recognizedText: String,
    val pronunciationScore: Int,
    val accuracyScore: Int,
    val speedWpm: Int,
    val missingWords: List<String>,
    val feedback: String
)

data class DialogueTurn(
    val sender: String, // "AI" or "USER"
    val text: String,
    val transliteration: String? = null,
    val translation: String,
    val audioPrompt: String? = null,
    val grammarCorrection: String? = null,
    val betterAlternative: String? = null
)

data class ConversationScenario(
    val id: String,
    val languageCode: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val level: String,
    val initialAiMessage: String,
    val initialAiTranslation: String,
    val initialAiTransliteration: String? = null,
    val sampleUserReplies: List<String>,
    val predefinedTurns: List<DialogueTurn> = emptyList()
)

data class ClickableWordAnnotation(
    val word: String,
    val meaning: String,
    val transliteration: String? = null,
    val pronunciation: String,
    val translation: String,
    val example: String
)

data class ReadingPassage(
    val id: String,
    val languageCode: String,
    val level: String, // Beginner, Elementary, Intermediate, Advanced
    val title: String,
    val content: String,
    val translation: String,
    val wordAnnotations: Map<String, ClickableWordAnnotation>,
    val comprehensionQuestions: List<QuizQuestion>
)

data class ListeningExercise(
    val id: String,
    val languageCode: String,
    val level: Int, // 1: Very slow, 2: Normal, 3: Natural, 4: Native fast
    val speechText: String,
    val speedMultiplier: Float, // 0.65f, 0.85f, 1.0f, 1.2f
    val transcript: String,
    val translation: String,
    val question: QuizQuestion
)

data class WritingExercise(
    val id: String,
    val languageCode: String,
    val prompt: String,
    val instruction: String,
    val starterText: String = "",
    val expectedPattern: String = "",
    val sampleAnswer: String,
    val commonMistakeChecks: List<WritingMistakeRule>
)

data class WritingMistakeRule(
    val regexPattern: String,
    val flaggedText: String,
    val correction: String,
    val reason: String
)

data class WritingFeedback(
    val userText: String,
    val score: Int,
    val correctedText: String,
    val detectedMistakes: List<WritingMistakeRule>,
    val generalFeedback: String
)

data class CourseLevel(
    val levelNumber: Int,
    val name: String,
    val description: String,
    val lessons: List<CourseLessonMeta>
)

data class CourseLessonMeta(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val category: String,
    val wordCount: Int,
    val estimatedMinutes: Int = 10,
    val isLocked: Boolean = false,
    val isCompleted: Boolean = false
)
