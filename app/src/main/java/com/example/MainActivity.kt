package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveAiAgentScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuizArenaScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TrainerViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TrainerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsState()

                if (!uiState.isOnboardingCompleted) {
                    OnboardingScreen(
                        onFinishOnboarding = { lang, level, nativeLang, reasons, focus, dailyGoal ->
                            viewModel.completeOnboarding(lang, level, nativeLang, reasons, focus, dailyGoal)
                        }
                    )
                } else {
                    LanguageTrainerApp(viewModel = viewModel)
                }
            }
        }
    }
}

sealed class AppNavTab(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : AppNavTab("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object QuizArena : AppNavTab("quiz_arena", "100 Quizzes", Icons.Filled.Psychology, Icons.Outlined.Psychology)
    object AiCoach : AppNavTab("ai_coach", "AI Partner", Icons.Filled.RecordVoiceOver, Icons.Outlined.RecordVoiceOver)
    object Practice : AppNavTab("practice", "Vault", Icons.Filled.MenuBook, Icons.Outlined.MenuBook)
    object Profile : AppNavTab("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun LanguageTrainerApp(viewModel: TrainerViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    var currentTab by remember { mutableStateOf<AppNavTab>(AppNavTab.Home) }
    var practiceInitialTab by remember { mutableIntStateOf(0) }
    var isSearchOpen by remember { mutableStateOf(false) }

    val navTabs = listOf(
        AppNavTab.Home,
        AppNavTab.QuizArena,
        AppNavTab.AiCoach,
        AppNavTab.Practice,
        AppNavTab.Profile
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                NavigationBar(modifier = Modifier.testTag("app_bottom_nav_bar")) {
                    navTabs.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (tab == AppNavTab.Practice) practiceInitialTab = 0
                                currentTab = tab
                            },
                            icon = {
                                if (tab == AppNavTab.Practice && uiState.activeMistakes.isNotEmpty()) {
                                    BadgedBox(
                                        badge = {
                                            Badge { Text("${uiState.activeMistakes.size}") }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.title
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.title
                                    )
                                }
                            },
                            label = { Text(tab.title) },
                            modifier = Modifier.testTag("bottom_tab_${tab.route}")
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    AppNavTab.Home -> HomeScreen(
                        uiState = uiState,
                        onLanguageSelected = { viewModel.switchLanguage(it) },
                        onSpeak = { text, lang -> viewModel.speakText(text, lang) },
                        onToggleSaveWord = { viewModel.toggleSaveWord(it) },
                        onNavigateToQuizArena = { category ->
                            if (category != null) {
                                viewModel.selectQuizCategory(category)
                            }
                            currentTab = AppNavTab.QuizArena
                        },
                        onNavigateToLiveAiAgent = {
                            currentTab = AppNavTab.AiCoach
                        },
                        onNavigateToPractice = {
                            practiceInitialTab = 0
                            currentTab = AppNavTab.Practice
                        },
                        onBuyRefillHearts = { viewModel.buyRefillHearts() },
                        onBuyStreakFreeze = { viewModel.buyStreakFreeze() },
                        onSaveProfileDetails = { name, email, phone, avatar ->
                            viewModel.updateProfileDetails(name, email, phone, avatar)
                        },
                        onNavigateToSearch = { isSearchOpen = true }
                    )

                    AppNavTab.QuizArena -> QuizArenaScreen(
                        uiState = uiState,
                        onLanguageSelected = { viewModel.switchLanguage(it) },
                        onSelectCategory = { viewModel.selectQuizCategory(it) },
                        onStartLevel = { category, lvl -> viewModel.startQuizLevel(category, lvl) },
                        onFinishLevel = { category, lvl, stars, score ->
                            viewModel.finishQuizLevel(category, lvl, stars, score)
                        },
                        onUseHeart = { viewModel.useHeartForMistake() },
                        onBuyRefillHearts = { viewModel.buyRefillHearts() },
                        onBuyStreakFreeze = { viewModel.buyStreakFreeze() },
                        onCloseQuiz = { viewModel.closeActiveQuiz() },
                        onSpeak = { text, lang -> viewModel.speakText(text, lang) }
                    )

                    AppNavTab.AiCoach -> LiveAiAgentScreen(
                        uiState = uiState,
                        onLanguageSelected = { viewModel.switchLanguage(it) },
                        onSendMessage = { viewModel.sendLiveAiAgentMessage(it) },
                        onStartMicListening = { onError -> viewModel.startLiveMicForAi(onError) },
                        onStopMicListening = { viewModel.stopListening() },
                        onSpeak = { text, lang -> viewModel.speakText(text, lang) }
                    )

                    AppNavTab.Practice -> PracticeScreen(
                        uiState = uiState,
                        onLanguageSelected = { viewModel.switchLanguage(it) },
                        onSpeak = { text, lang -> viewModel.speakText(text, lang) },
                        onUpdateSpacedRepetition = { item, remembered, difficulty ->
                            viewModel.recordSpacedRepetitionResult(item, remembered, difficulty)
                        },
                        onResolveMistake = { viewModel.resolveMistake(it) },
                        onRecordMistake = { cat, prompt, userAns, correctAns, exp ->
                            viewModel.recordMistake(cat, prompt, userAns, correctAns, exp)
                        },
                        onEvaluateWriting = { exercise, userText ->
                            viewModel.evaluateWriting(exercise, userText)
                        },
                        initialTab = practiceInitialTab
                    )

                    AppNavTab.Profile -> ProfileScreen(
                        uiState = uiState,
                        onLanguageSelected = { viewModel.switchLanguage(it) },
                        onSaveProfileDetails = { name, email, phone, avatar ->
                            viewModel.updateProfileDetails(name, email, phone, avatar)
                        },
                        onToggleNotifications = { viewModel.toggleNotifications(it) },
                        onResetOnboarding = { viewModel.resetOnboarding() }
                    )
                }
            }
        }

        // Global Search Fullscreen Layer
        AnimatedVisibility(
            visible = isSearchOpen,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            SearchScreen(
                languageName = uiState.currentLanguage.displayName,
                languageCode = uiState.currentLanguage.code,
                searchResultsWords = uiState.searchResultsWords,
                searchResultsGrammar = uiState.searchResultsGrammar,
                onQueryChange = { viewModel.search(it) },
                onSpeak = { text, lang -> viewModel.speakText(text, lang) },
                onBack = { isSearchOpen = false }
            )
        }
    }
}
