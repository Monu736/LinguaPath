package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Achievement
import com.example.data.model.ConversationScenario
import com.example.data.model.DailyChallenge
import com.example.data.model.DialogueTurn
import com.example.data.model.GrammarLesson
import com.example.data.model.LanguageProgress
import com.example.data.model.LifestyleCategory
import com.example.data.model.MistakeItem
import com.example.data.model.QuizLevelProgress
import com.example.data.model.SpeakingEvaluation
import com.example.data.model.SpeakingPrompt
import com.example.data.model.SupportedLanguage
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyItem
import com.example.data.model.WritingExercise
import com.example.data.model.WritingFeedback
import com.example.data.repository.LanguageRepository
import com.example.quiz.CategoryQuizData
import com.example.quiz.QuizContentGenerator
import com.example.trainer.AiTrainerService
import com.example.trainer.PersonalizationEngine
import com.example.trainer.SpeechManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrainerUiState(
    val userProfile: UserProfile? = null,
    val currentLanguage: SupportedLanguage = SupportedLanguage.ENGLISH,
    val languageProgress: LanguageProgress? = null,
    val allProgress: List<LanguageProgress> = emptyList(),
    val vocabulary: List<VocabularyItem> = emptyList(),
    val wordsDueForReview: List<VocabularyItem> = emptyList(),
    val savedWords: List<VocabularyItem> = emptyList(),
    val grammarLessons: List<GrammarLesson> = emptyList(),
    val activeMistakes: List<MistakeItem> = emptyList(),
    val dailyChallenge: DailyChallenge? = null,
    val achievements: List<Achievement> = emptyList(),
    val wordOfTheDay: VocabularyItem? = null,
    val todayPlan: List<String> = emptyList(),
    val isSpeaking: Boolean = false,
    val isListeningToMic: Boolean = false,
    val activeScenario: ConversationScenario? = null,
    val scenarioTurns: List<DialogueTurn> = emptyList(),
    val isAiThinking: Boolean = false,
    val lastSpeakingEvaluation: SpeakingEvaluation? = null,
    val writingFeedback: WritingFeedback? = null,
    val searchResultsWords: List<VocabularyItem> = emptyList(),
    val searchResultsGrammar: List<GrammarLesson> = emptyList(),
    // 100-Level Quiz Engine
    val selectedQuizCategory: LifestyleCategory = LifestyleCategory.FOOD,
    val categoryLevels: List<QuizLevelProgress> = emptyList(),
    val activeQuizLevelData: CategoryQuizData? = null,
    // Live AI Coach
    val liveAiDialogueHistory: List<DialogueTurn> = emptyList(),
    val isLiveAiSpeaking: Boolean = false,
    val liveAiStatusText: String = "AI Coach is ready. Speak live or type to learn!"
) {
    val isOnboardingCompleted: Boolean
        get() = userProfile?.isOnboardingCompleted != false // Defaults to true
}

class TrainerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    val repository = LanguageRepository(db)
    val speechManager = SpeechManager(application)
    private val aiTrainerService = AiTrainerService()

    private val _uiState = MutableStateFlow(TrainerUiState())
    val uiState: StateFlow<TrainerUiState> = _uiState.asStateFlow()

    private var languageObserverJob: Job? = null
    private var categoryLevelsJob: Job? = null

    init {
        // Collect User Profile
        viewModelScope.launch {
            repository.userProfile.collect { profile ->
                _uiState.update { it.copy(userProfile = profile) }
                if (profile != null) {
                    val lang = SupportedLanguage.fromCode(profile.currentLanguageCode)
                    if (_uiState.value.currentLanguage != lang) {
                        _uiState.update { it.copy(currentLanguage = lang) }
                        observeLanguageData(lang.code)
                    }
                }
            }
        }

        // Collect Achievements
        viewModelScope.launch {
            repository.achievements.collect { list ->
                _uiState.update { it.copy(achievements = list) }
            }
        }

        // Collect All Language Progress
        viewModelScope.launch {
            repository.allProgress.collect { list ->
                _uiState.update { it.copy(allProgress = list) }
            }
        }

        // Start observing initial language & category data
        observeLanguageData(_uiState.value.currentLanguage.code)
        observeCategoryLevels(_uiState.value.currentLanguage.code, _uiState.value.selectedQuizCategory.key)
        initializeAiCoachDefaults(_uiState.value.currentLanguage)
    }

    private fun observeLanguageData(langCode: String) {
        languageObserverJob?.cancel()
        languageObserverJob = viewModelScope.launch {
            launch {
                repository.getLanguageProgress(langCode).collect { prog ->
                    _uiState.update { it.copy(languageProgress = prog) }
                }
            }
            launch {
                repository.getVocabulary(langCode).collect { vocab ->
                    val wotd = vocab.firstOrNull { it.isSaved } ?: vocab.firstOrNull()
                    _uiState.update {
                        it.copy(
                            vocabulary = vocab,
                            wordOfTheDay = wotd
                        )
                    }
                }
            }
            launch {
                repository.getWordsDueForReview(langCode).collect { due ->
                    val plan = PersonalizationEngine.generateDailyPlan(langCode, due.size)
                    _uiState.update {
                        it.copy(
                            wordsDueForReview = due,
                            todayPlan = plan
                        )
                    }
                }
            }
            launch {
                repository.getSavedWords(langCode).collect { saved ->
                    _uiState.update { it.copy(savedWords = saved) }
                }
            }
            launch {
                repository.getGrammarLessons(langCode).collect { lessons ->
                    _uiState.update { it.copy(grammarLessons = lessons) }
                }
            }
            launch {
                repository.getActiveMistakes(langCode).collect { mistakes ->
                    _uiState.update { it.copy(activeMistakes = mistakes) }
                }
            }
            launch {
                repository.getTodayChallenge(langCode).collect { challenge ->
                    _uiState.update { it.copy(dailyChallenge = challenge) }
                }
            }
        }
    }

    private fun observeCategoryLevels(langCode: String, categoryKey: String) {
        categoryLevelsJob?.cancel()
        categoryLevelsJob = viewModelScope.launch {
            repository.getLevelsForCategory(langCode, categoryKey).collect { levels ->
                // If database hasn't populated all 100 yet or empty, provide instant responsive level list
                val fullLevels = if (levels.size < 100) {
                    val map = levels.associateBy { it.levelNumber }
                    (1..100).map { lvl ->
                        map[lvl] ?: QuizLevelProgress(
                            id = "${langCode}_${categoryKey}_$lvl",
                            languageCode = langCode,
                            categoryKey = categoryKey,
                            levelNumber = lvl,
                            stars = if (lvl == 1) 3 else if (lvl == 2) 2 else 0,
                            isUnlocked = lvl <= 3,
                            highScore = if (lvl == 1) 100 else if (lvl == 2) 80 else 0
                        )
                    }
                } else {
                    levels
                }
                _uiState.update { it.copy(categoryLevels = fullLevels) }
            }
        }
    }

    private fun initializeAiCoachDefaults(language: SupportedLanguage) {
        val greeting = when (language) {
            SupportedLanguage.HINDI -> DialogueTurn(
                sender = "AI",
                text = "नमस्ते! मैं आपका पर्सनल AI लैंग्वेज पार्टनर हूँ। आज आप क्या सीखना चाहते हैं?",
                transliteration = "Namaste! Main aapka personal AI language partner hoon. Aaj aap kya seekhna chahte hain?",
                translation = "Hello! I am your personal AI language partner. What would you like to learn today?"
            )
            SupportedLanguage.MARATHI -> DialogueTurn(
                sender = "AI",
                text = "नमस्कार! मी तुमचा AI भाषा मार्गदर्शक आहे. बोला, आज काय सराव करायचा आहे?",
                transliteration = "Namaskar! Mee tumcha AI bhasha margadarshak ahe. Bola, aaj kay sarav karaycha ahe?",
                translation = "Hello! I am your AI language coach. Tell me, what would you like to practice today?"
            )
            SupportedLanguage.JAPANESE -> DialogueTurn(
                sender = "AI",
                text = "こんにちは！私はあなたのAIパートナーです。日本語で話しましょう！",
                transliteration = "Konnichiwa! Watashi wa anata no AI paatonaa desu. Nihongo de hanashimashou!",
                translation = "Hello! I am your AI partner. Let's speak in Japanese!"
            )
            else -> DialogueTurn(
                sender = "AI",
                text = "Hey there! 👋 I'm your Live AI Trainer. Let's talk, practice real conversations, and level up your English!",
                transliteration = null,
                translation = "Hello! Let's practice speaking and learning together."
            )
        }
        _uiState.update { it.copy(liveAiDialogueHistory = listOf(greeting)) }
    }

    fun switchLanguage(language: SupportedLanguage) {
        _uiState.update {
            it.copy(
                currentLanguage = language,
                activeScenario = null,
                scenarioTurns = emptyList(),
                activeQuizLevelData = null
            )
        }
        observeLanguageData(language.code)
        observeCategoryLevels(language.code, _uiState.value.selectedQuizCategory.key)
        initializeAiCoachDefaults(language)
        viewModelScope.launch {
            repository.switchLanguage(language.code)
        }
    }

    fun updateProfileDetails(name: String, email: String, phone: String, avatar: String) {
        viewModelScope.launch {
            repository.updateProfileDetails(name, email, phone, avatar)
            _uiState.update {
                it.copy(
                    userProfile = it.userProfile?.copy(
                        userName = name,
                        userEmail = email,
                        userPhone = phone,
                        avatarEmoji = avatar,
                        isLoggedIn = true
                    )
                )
            }
        }
    }

    fun completeOnboarding(
        targetLanguage: SupportedLanguage,
        level: String,
        nativeLanguage: String,
        reasons: List<String>,
        focusAreas: List<String>,
        dailyGoalMinutes: Int
    ) {
        viewModelScope.launch {
            val updated = UserProfile(
                id = 1,
                userName = _uiState.value.userProfile?.userName ?: "Monu Gupta",
                userEmail = _uiState.value.userProfile?.userEmail ?: "monugupta7478@gmail.com",
                userPhone = _uiState.value.userProfile?.userPhone ?: "+91 98765 43210",
                isLoggedIn = true,
                avatarEmoji = _uiState.value.userProfile?.avatarEmoji ?: "⚡",
                currentLanguageCode = targetLanguage.code,
                nativeLanguage = nativeLanguage,
                currentLevel = level,
                reasons = reasons.joinToString(", "),
                improvementFocus = focusAreas.joinToString(", "),
                dailyGoalMinutes = dailyGoalMinutes,
                isOnboardingCompleted = true,
                xp = 240,
                streak = 3,
                lastActiveDate = "2026-10-05",
                wordsLearnedCount = 20,
                lessonsCompletedCount = 6,
                overallAccuracy = 90
            )
            repository.saveUserProfile(updated)
            switchLanguage(targetLanguage)
        }
    }

    fun resetOnboarding() {
        val current = _uiState.value.userProfile
        if (current != null) {
            viewModelScope.launch {
                repository.saveUserProfile(current.copy(isOnboardingCompleted = false))
            }
        }
    }

    // ==========================================
    // 100-LEVEL QUIZ ENGINE ACTIONS
    // ==========================================

    fun selectQuizCategory(category: LifestyleCategory) {
        _uiState.update { it.copy(selectedQuizCategory = category) }
        observeCategoryLevels(_uiState.value.currentLanguage.code, category.key)
    }

    fun startQuizLevel(category: LifestyleCategory, levelNumber: Int) {
        val quizData = QuizContentGenerator.generateQuizForLevel(
            category = category,
            levelNumber = levelNumber,
            language = _uiState.value.currentLanguage
        )
        _uiState.update { it.copy(activeQuizLevelData = quizData) }
    }

    fun finishQuizLevel(category: LifestyleCategory, levelNumber: Int, stars: Int, score: Int) {
        viewModelScope.launch {
            repository.completeQuizLevel(
                langCode = _uiState.value.currentLanguage.code,
                categoryKey = category.key,
                levelNumber = levelNumber,
                stars = stars,
                score = score
            )
            // Bonus Gems reward for completing level
            val gemsEarned = 10 + stars * 5
            repository.addGems(gemsEarned)
        }
    }

    fun useHeartForMistake() {
        viewModelScope.launch {
            repository.useHeart()
        }
    }

    fun buyRefillHearts() {
        viewModelScope.launch {
            repository.buyRefillHearts()
        }
    }

    fun buyStreakFreeze() {
        viewModelScope.launch {
            repository.buyStreakFreeze()
        }
    }

    fun closeActiveQuiz() {
        _uiState.update { it.copy(activeQuizLevelData = null) }
    }

    // ==========================================
    // LIVE AI AGENT ACTIONS (Talking live & answering back)
    // ==========================================

    fun sendLiveAiAgentMessage(userText: String, autoSpeakReply: Boolean = true) {
        if (userText.isBlank()) return
        val currentLang = _uiState.value.currentLanguage
        val userTurn = DialogueTurn(
            sender = "USER",
            text = userText,
            translation = ""
        )
        val updatedHistory = _uiState.value.liveAiDialogueHistory + userTurn
        _uiState.update {
            it.copy(
                liveAiDialogueHistory = updatedHistory,
                isAiThinking = true,
                liveAiStatusText = "AI Coach is typing & speaking back..."
            )
        }

        viewModelScope.launch {
            val scenario = ConversationScenario(
                id = "live_coach_${currentLang.code}",
                languageCode = currentLang.code,
                title = "Live Language Trainer",
                description = "Real-time friendly language practice and mistake correction",
                iconEmoji = "🤖",
                level = _uiState.value.userProfile?.currentLevel ?: "Beginner",
                initialAiMessage = "",
                initialAiTranslation = "",
                sampleUserReplies = emptyList()
            )

            val aiTurn = aiTrainerService.sendScenarioDialogue(
                scenario = scenario,
                history = updatedHistory,
                userMessage = userText
            )

            _uiState.update {
                it.copy(
                    isAiThinking = false,
                    liveAiDialogueHistory = it.liveAiDialogueHistory + aiTurn,
                    liveAiStatusText = "AI Coach spoke back! Tap mic to reply."
                )
            }

            repository.addXp(currentLang.code, 15)

            if (autoSpeakReply) {
                speakText(aiTurn.text, currentLang.code)
            }
        }
    }

    fun startLiveMicForAi(onError: (String) -> Unit) {
        val currentLang = _uiState.value.currentLanguage
        _uiState.update { it.copy(liveAiStatusText = "Listening live... speak now! 🎙️") }
        speechManager.startListening(
            languageCode = currentLang.code,
            onResult = { spokenText ->
                _uiState.update { it.copy(isListeningToMic = false) }
                if (spokenText.isNotBlank()) {
                    sendLiveAiAgentMessage(spokenText, autoSpeakReply = true)
                }
            },
            onError = { err ->
                _uiState.update {
                    it.copy(
                        isListeningToMic = false,
                        liveAiStatusText = "Tap mic to speak or type below."
                    )
                }
                onError(err)
            },
            onListeningState = { listening ->
                _uiState.update { it.copy(isListeningToMic = listening) }
            }
        )
    }

    // ==========================================
    // OTHER LEARNING & AUDIO ACTIONS
    // ==========================================

    fun toggleSaveWord(id: Int) {
        viewModelScope.launch {
            repository.toggleSavedWord(id)
        }
    }

    fun recordSpacedRepetitionResult(item: VocabularyItem, isCorrect: Boolean, confidence: Int) {
        viewModelScope.launch {
            repository.updateVocabularySpacedRepetition(item, isCorrect, confidence)
        }
    }

    fun resolveMistake(id: Int) {
        viewModelScope.launch {
            repository.resolveMistake(id)
        }
    }

    fun completeLesson(lessonId: Int, score: Int) {
        viewModelScope.launch {
            repository.completeLesson(_uiState.value.currentLanguage.code, lessonId, score)
        }
    }

    fun recordMistake(category: String, prompt: String, userAnswer: String, correctAnswer: String, explanation: String) {
        viewModelScope.launch {
            repository.recordMistake(
                langCode = _uiState.value.currentLanguage.code,
                category = category,
                prompt = prompt,
                userAnswer = userAnswer,
                correctAnswer = correctAnswer,
                explanation = explanation
            )
        }
    }

    fun speakText(text: String, languageCode: String = _uiState.value.currentLanguage.code, rate: Float = 1.0f) {
        _uiState.update { it.copy(isSpeaking = true, isLiveAiSpeaking = true) }
        speechManager.speak(
            text = text,
            languageCode = languageCode,
            speechRate = rate,
            onStart = {
                _uiState.update { it.copy(isSpeaking = true, isLiveAiSpeaking = true) }
            },
            onDone = {
                _uiState.update { it.copy(isSpeaking = false, isLiveAiSpeaking = false) }
            }
        )
    }

    fun stopSpeaking() {
        speechManager.stopSpeaking()
        _uiState.update { it.copy(isSpeaking = false, isLiveAiSpeaking = false) }
    }

    fun startListeningForPrompt(
        targetPrompt: SpeakingPrompt,
        onEvaluationDone: (SpeakingEvaluation) -> Unit,
        onError: (String) -> Unit
    ) {
        speechManager.startListening(
            languageCode = targetPrompt.languageCode,
            onResult = { spokenText ->
                _uiState.update { it.copy(isListeningToMic = false) }
                val eval = speechManager.evaluateSpeech(targetPrompt.sentence, spokenText)
                _uiState.update { it.copy(lastSpeakingEvaluation = eval) }
                onEvaluationDone(eval)
                if (eval.accuracyScore >= 70) {
                    viewModelScope.launch {
                        repository.addXp(targetPrompt.languageCode, 15)
                    }
                } else {
                    recordMistake(
                        category = "Pronunciation",
                        prompt = targetPrompt.sentence,
                        userAnswer = spokenText.ifEmpty { "[Unclear speech]" },
                        correctAnswer = targetPrompt.sentence,
                        explanation = eval.feedback
                    )
                }
            },
            onError = { err ->
                _uiState.update { it.copy(isListeningToMic = false) }
                onError(err)
            },
            onListeningState = { listening ->
                _uiState.update { it.copy(isListeningToMic = listening) }
            }
        )
    }

    fun stopListening() {
        speechManager.stopListening()
        _uiState.update { it.copy(isListeningToMic = false) }
    }

    fun startScenario(scenario: ConversationScenario) {
        val initialTurn = DialogueTurn(
            sender = "AI",
            text = scenario.initialAiMessage,
            transliteration = scenario.initialAiTransliteration,
            translation = scenario.initialAiTranslation
        )
        _uiState.update {
            it.copy(
                activeScenario = scenario,
                scenarioTurns = listOf(initialTurn)
            )
        }
    }

    fun sendScenarioUserMessage(userMessage: String) {
        val scenario = _uiState.value.activeScenario ?: return
        if (userMessage.isBlank()) return

        val userTurn = DialogueTurn(
            sender = "USER",
            text = userMessage,
            translation = ""
        )
        val updatedTurns = _uiState.value.scenarioTurns + userTurn
        _uiState.update { it.copy(scenarioTurns = updatedTurns, isAiThinking = true) }

        viewModelScope.launch {
            val aiTurn = aiTrainerService.sendScenarioDialogue(
                scenario = scenario,
                history = updatedTurns,
                userMessage = userMessage
            )
            _uiState.update {
                it.copy(
                    isAiThinking = false,
                    scenarioTurns = it.scenarioTurns + aiTurn
                )
            }
            repository.addXp(scenario.languageCode, 10)
            speakText(aiTurn.text, scenario.languageCode)
        }
    }

    fun evaluateWriting(exercise: WritingExercise, userText: String) {
        viewModelScope.launch {
            val feedback = aiTrainerService.evaluateWriting(exercise.prompt, userText, exercise.commonMistakeChecks)
            _uiState.update { it.copy(writingFeedback = feedback) }
            if (feedback.detectedMistakes.isNotEmpty()) {
                val firstMistake = feedback.detectedMistakes.first()
                recordMistake(
                    category = "Grammar",
                    prompt = exercise.prompt,
                    userAnswer = firstMistake.flaggedText,
                    correctAnswer = firstMistake.correction,
                    explanation = firstMistake.reason
                )
            } else {
                repository.addXp(exercise.languageCode, 20)
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResultsWords = emptyList(), searchResultsGrammar = emptyList()) }
            return
        }
        viewModelScope.launch {
            val (words, grammar) = repository.searchAll(_uiState.value.currentLanguage.code, query)
            _uiState.update { it.copy(searchResultsWords = words, searchResultsGrammar = grammar) }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            db.userDao().updateNotifications(enabled)
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.release()
    }
}
