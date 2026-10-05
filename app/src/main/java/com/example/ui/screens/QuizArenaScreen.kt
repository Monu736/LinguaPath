package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LifestyleCategory
import com.example.data.model.QuizLevelProgress
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizType
import com.example.data.model.SupportedLanguage
import com.example.quiz.CategoryQuizData
import com.example.ui.components.DuoFeedbackBanner
import com.example.ui.components.DuoLeaguesDialog
import com.example.ui.components.DuoOptionCard
import com.example.ui.components.DuoOptionState
import com.example.ui.components.DuoShopDialog
import com.example.ui.components.DuoTactileButton
import com.example.ui.components.DuoTopBar
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.DuoBlue
import com.example.ui.theme.DuoBlueDark
import com.example.ui.theme.DuoGem
import com.example.ui.theme.DuoGold
import com.example.ui.theme.DuoGoldDark
import com.example.ui.theme.DuoGreen
import com.example.ui.theme.DuoGreenDark
import com.example.ui.theme.DuoRed
import com.example.ui.theme.DuoRedDark
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseAccent
import com.example.ui.viewmodel.TrainerUiState
import kotlinx.coroutines.launch

@Composable
fun QuizArenaScreen(
    uiState: TrainerUiState,
    onLanguageSelected: (SupportedLanguage) -> Unit,
    onSelectCategory: (LifestyleCategory) -> Unit,
    onStartLevel: (LifestyleCategory, Int) -> Unit,
    onFinishLevel: (LifestyleCategory, Int, Int, Int) -> Unit,
    onUseHeart: () -> Unit,
    onBuyRefillHearts: () -> Unit,
    onBuyStreakFreeze: () -> Unit,
    onCloseQuiz: () -> Unit,
    onSpeak: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLang = uiState.currentLanguage
    val profile = uiState.userProfile
    val selectedCategory = uiState.selectedQuizCategory
    val levels = uiState.categoryLevels
    val activeQuiz = uiState.activeQuizLevelData

    var isShopDialogOpen by remember { mutableStateOf(false) }
    var isLeaguesDialogOpen by remember { mutableStateOf(false) }
    var viewModeWindingPath by remember { mutableStateOf(true) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Duolingo Top Stats Bar
            DuoTopBar(
                currentLanguage = currentLang,
                streak = profile?.streak ?: 3,
                gems = profile?.gems ?: 450,
                hearts = profile?.hearts ?: 5,
                onLanguageClick = {
                    val nextLang = when (currentLang) {
                        SupportedLanguage.ENGLISH -> SupportedLanguage.HINDI
                        SupportedLanguage.HINDI -> SupportedLanguage.MARATHI
                        SupportedLanguage.MARATHI -> SupportedLanguage.JAPANESE
                        SupportedLanguage.JAPANESE -> SupportedLanguage.ENGLISH
                    }
                    onLanguageSelected(nextLang)
                },
                onStreakClick = { isShopDialogOpen = true },
                onGemsClick = { isShopDialogOpen = true },
                onHeartsClick = { isShopDialogOpen = true }
            )

            // Category Horizontal Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lifestyle Categories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )

                // View Mode Toggle (Winding S-Curve Path vs Grid)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { viewModeWindingPath = !viewModeWindingPath }
                ) {
                    Text(
                        text = if (viewModeWindingPath) "🐍 Winding Path" else "🗺️ Grid Map",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = DuoBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(LifestyleCategory.entries) { category ->
                    val isSelected = category == selectedCategory
                    val accentColor = Color(category.accentHex)

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) accentColor else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) BorderStroke(1.5.dp, Color.White.copy(alpha = 0.8f)) else null,
                        modifier = Modifier
                            .clickable { onSelectCategory(category) }
                            .testTag("category_tab_${category.key}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(text = category.emoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            if (category.isTrending) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) Color.White.copy(alpha = 0.25f) else AmberTertiary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "HOT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) Color.White else AmberTertiary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 100-Level Map View for Selected Category (Winding Path or Grid)
            if (viewModeWindingPath) {
                DuoWindingPathContent(
                    category = selectedCategory,
                    levels = levels,
                    onStartLevel = { levelNum -> onStartLevel(selectedCategory, levelNum) }
                )
            } else {
                LevelMapContent(
                    category = selectedCategory,
                    levels = levels,
                    onStartLevel = { levelNum -> onStartLevel(selectedCategory, levelNum) }
                )
            }
        }

        // Active Interactive Quiz Runner Overlay
        AnimatedVisibility(
            visible = activeQuiz != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            if (activeQuiz != null) {
                InteractiveDuoQuizRunner(
                    quizData = activeQuiz,
                    language = currentLang,
                    currentHearts = profile?.hearts ?: 5,
                    onSpeak = onSpeak,
                    onUseHeart = onUseHeart,
                    onFinishQuiz = { stars, score ->
                        onFinishLevel(selectedCategory, activeQuiz.levelNumber, stars, score)
                    },
                    onClose = onCloseQuiz,
                    onOpenShop = { isShopDialogOpen = true }
                )
            }
        }

        // Duolingo Gem Shop Dialog
        if (isShopDialogOpen) {
            DuoShopDialog(
                userGems = profile?.gems ?: 450,
                streakFreezes = profile?.streakFreezeCount ?: 2,
                currentHearts = profile?.hearts ?: 5,
                onBuyRefill = {
                    onBuyRefillHearts()
                    isShopDialogOpen = false
                },
                onBuyFreeze = {
                    onBuyStreakFreeze()
                    isShopDialogOpen = false
                },
                onDismiss = { isShopDialogOpen = false }
            )
        }

        // Duolingo Leagues Leaderboard Dialog
        if (isLeaguesDialogOpen) {
            DuoLeaguesDialog(
                userRank = profile?.leagueRank ?: 4,
                userXp = profile?.xp ?: 240,
                currentLeague = profile?.currentLeague ?: "Ruby League",
                onDismiss = { isLeaguesDialogOpen = false }
            )
        }
    }
}

// ==========================================
// DUOLINGO WINDING S-CURVE STEPPING-STONE PATH (THE TREE)
// ==========================================
@Composable
fun DuoWindingPathContent(
    category: LifestyleCategory,
    levels: List<QuizLevelProgress>,
    onStartLevel: (Int) -> Unit
) {
    val accentColor = Color(category.accentHex)
    val totalStars = levels.sumOf { it.stars }

    val full100Levels = remember(levels) {
        val map = levels.associateBy { it.levelNumber }
        (1..100).map { lvl ->
            map[lvl] ?: QuizLevelProgress(
                id = "${category.key}_$lvl",
                languageCode = "en",
                categoryKey = category.key,
                levelNumber = lvl,
                stars = if (lvl == 1) 3 else 0,
                isUnlocked = lvl <= 2,
                highScore = if (lvl == 1) 100 else 0
            )
        }
    }

    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .testTag("duo_winding_path")
    ) {
        // Unit Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = accentColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${category.emoji} ${category.title}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Level 1 to 100 • $totalStars / 300 Stars Collected",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = "UNIT 1",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Stepping Stone Circles winding left and right
        items(full100Levels) { levelItem ->
            val lvl = levelItem.levelNumber
            // S-Curve horizontal offset
            val xOffset: Dp = when ((lvl - 1) % 8) {
                0 -> 0.dp
                1 -> (-50).dp
                2 -> (-80).dp
                3 -> (-45).dp
                4 -> 0.dp
                5 -> 50.dp
                6 -> 80.dp
                7 -> 45.dp
                else -> 0.dp
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(x = xOffset)
                    .padding(vertical = 8.dp)
            ) {
                DuoPathSteppingStone(
                    level = levelItem,
                    accentColor = accentColor,
                    onClick = { onStartLevel(lvl) }
                )

                // Every 10 levels show a milestone gift chest
                if (lvl % 10 == 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = CircleShape,
                        color = DuoGold.copy(alpha = 0.2f),
                        border = BorderStroke(2.dp, DuoGold),
                        modifier = Modifier
                            .size(46.dp)
                            .clickable { onStartLevel(lvl) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🎁", fontSize = 22.sp)
                        }
                    }
                    Text(
                        text = "Bonus Chest!",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = DuoGoldDark
                    )
                }
            }
        }
    }
}

@Composable
fun DuoPathSteppingStone(
    level: QuizLevelProgress,
    accentColor: Color,
    onClick: () -> Unit
) {
    val isUnlocked = level.isUnlocked
    val stars = level.stars

    val (faceColor, shadowColor) = when {
        !isUnlocked -> Color(0xFFE5E5E5) to Color(0xFFCECECE)
        stars == 3 -> DuoGold to DuoGoldDark
        stars in 1..2 -> DuoGreen to DuoGreenDark
        else -> accentColor to Color(0xFF333333)
    }

    Box(
        modifier = Modifier
            .size(76.dp)
            .clickable(enabled = isUnlocked) { onClick() }
            .testTag("duo_node_${level.levelNumber}"),
        contentAlignment = Alignment.Center
    ) {
        // Bottom bevel shadow circle
        Box(
            modifier = Modifier
                .size(72.dp)
                .offset(y = 6.dp)
                .clip(CircleShape)
                .background(shadowColor)
        )

        // Top main circle
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(faceColor)
                .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isUnlocked) {
                    Text(
                        text = if (stars == 3) "⭐" else "${level.levelNumber}",
                        fontSize = if (stars == 3) 24.sp else 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    if (stars > 0 && stars < 3) {
                        Row(horizontalArrangement = Arrangement.Center) {
                            repeat(stars) {
                                Text(text = "★", fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LevelMapContent(
    category: LifestyleCategory,
    levels: List<QuizLevelProgress>,
    onStartLevel: (Int) -> Unit
) {
    val accentColor = Color(category.accentHex)

    val full100Levels = remember(levels) {
        val map = levels.associateBy { it.levelNumber }
        (1..100).map { lvl ->
            map[lvl] ?: QuizLevelProgress(
                id = "${category.key}_$lvl",
                languageCode = "en",
                categoryKey = category.key,
                levelNumber = lvl,
                stars = if (lvl == 1) 3 else 0,
                isUnlocked = lvl <= 2,
                highScore = if (lvl == 1) 100 else 0
            )
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("quiz_level_grid_list")
    ) {
        val chunkedLevels = full100Levels.chunked(4)
        items(chunkedLevels) { rowLevels ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowLevels.forEach { levelItem ->
                    DuoTactileButton(
                        text = if (levelItem.isUnlocked) "${levelItem.levelNumber}" else "🔒",
                        onClick = { onStartLevel(levelItem.levelNumber) },
                        enabled = levelItem.isUnlocked,
                        faceColor = if (levelItem.stars > 0) DuoGold else accentColor,
                        shadowColor = if (levelItem.stars > 0) DuoGoldDark else Color(0xFF333333),
                        height = 60.dp,
                        bevelDepth = 3.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(4 - rowLevels.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// ==========================================
// DUOLINGO INTERACTIVE QUIZ RUNNER WITH HEARTS & ANIMATED FEEDBACK
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveDuoQuizRunner(
    quizData: CategoryQuizData,
    language: SupportedLanguage,
    currentHearts: Int,
    onSpeak: (String, String) -> Unit,
    onUseHeart: () -> Unit,
    onFinishQuiz: (stars: Int, score: Int) -> Unit,
    onClose: () -> Unit,
    onOpenShop: () -> Unit
) {
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isAnswerChecked by remember { mutableStateOf(false) }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }
    var comboCount by remember { mutableIntStateOf(0) }
    var isQuizCompleted by remember { mutableStateOf(false) }
    var localHearts by remember { mutableIntStateOf(currentHearts) }
    var showOutOfHeartsDialog by remember { mutableStateOf(false) }

    // Scrambled Sentence Builder State
    val placedWords = remember { mutableStateListOf<String>() }
    val remainingWords = remember { mutableStateListOf<String>() }

    val currentQuestion = quizData.questions.getOrNull(currentQuestionIndex)

    LaunchedEffect(currentQuestionIndex) {
        selectedAnswer = null
        isAnswerChecked = false
        isAnswerCorrect = false
        placedWords.clear()
        remainingWords.clear()
        currentQuestion?.let { q ->
            if (q.type == QuizType.SENTENCE_ARRANGEMENT) {
                remainingWords.addAll(q.scrambledWords)
            }
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .testTag("interactive_duo_quiz_runner")
    ) {
        if (isQuizCompleted) {
            DuoCelebrationScreen(
                title = quizData.title,
                totalQuestions = quizData.questions.size,
                correctCount = correctCount,
                xpReward = quizData.xpReward,
                combo = comboCount,
                onFinish = { stars, score -> onFinishQuiz(stars, score) },
                onClose = onClose
            )
        } else if (currentQuestion != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Top Progress Bar + Hearts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onClose) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }

                        // Duolingo Smooth Progress Bar
                        LinearProgressIndicator(
                            progress = { (currentQuestionIndex + 1).toFloat() / quizData.questions.size },
                            color = DuoGreen,
                            trackColor = Color(0xFFE5E5E5),
                            modifier = Modifier
                                .weight(1f)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp)
                        )

                        // Hearts indicator with animated crack/red
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DuoRed.copy(alpha = 0.15f),
                            modifier = Modifier.clickable { onOpenShop() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "❤️", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$localHearts",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DuoRed,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }

                    // Combo Streak Indicator
                    if (comboCount >= 2) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DuoGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "🔥 $comboCount IN A ROW! COMBO 2X XP",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DuoGoldDark,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Question Prompt with Mascot Speech Bubble
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Friendly Coach Mascot Icon
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(DuoGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🦉", fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = currentQuestion.question,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    if (currentQuestion.audioText != null) {
                                        IconButton(
                                            onClick = { onSpeak(currentQuestion.audioText, language.code) },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(DuoBlue.copy(alpha = 0.2f))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Listen",
                                                tint = DuoBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                if (!currentQuestion.subtitle.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentQuestion.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Interactive Question Options with 3D Transitions
                    when (currentQuestion.type) {
                        QuizType.SENTENCE_ARRANGEMENT -> {
                            // Placed word slots
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(2.dp, DuoBlue.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                            ) {
                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    placedWords.forEachIndexed { idx, word ->
                                        DuoTactileButton(
                                            text = word,
                                            onClick = {
                                                placedWords.removeAt(idx)
                                                remainingWords.add(word)
                                            },
                                            faceColor = DuoBlue,
                                            shadowColor = DuoBlueDark,
                                            height = 40.dp,
                                            bevelDepth = 3.dp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "Tap words to place in sentence:", style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(6.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                remainingWords.forEachIndexed { idx, word ->
                                    DuoTactileButton(
                                        text = word,
                                        onClick = {
                                            placedWords.add(word)
                                            remainingWords.removeAt(idx)
                                        },
                                        faceColor = MaterialTheme.colorScheme.surface,
                                        shadowColor = Color(0xFFCECECE),
                                        textColor = MaterialTheme.colorScheme.onSurface,
                                        height = 42.dp,
                                        bevelDepth = 3.dp
                                    )
                                }
                            }
                        }

                        else -> {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                currentQuestion.options.forEachIndexed { optIndex, opt ->
                                    val isSelected = selectedAnswer == opt
                                    val optState = when {
                                        isAnswerChecked && opt == currentQuestion.correctAnswer -> DuoOptionState.CORRECT
                                        isAnswerChecked && isSelected && opt != currentQuestion.correctAnswer -> DuoOptionState.WRONG
                                        isSelected -> DuoOptionState.SELECTED
                                        else -> DuoOptionState.DEFAULT
                                    }

                                    DuoOptionCard(
                                        text = opt,
                                        state = optState,
                                        leadingBadge = "${optIndex + 1}",
                                        onClick = {
                                            if (!isAnswerChecked) {
                                                selectedAnswer = opt
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Tactile Check Answer Button
                    if (!isAnswerChecked) {
                        DuoTactileButton(
                            text = "CHECK ANSWER",
                            onClick = {
                                val userAns = if (currentQuestion.type == QuizType.SENTENCE_ARRANGEMENT) {
                                    placedWords.joinToString(" ")
                                } else {
                                    selectedAnswer ?: ""
                                }
                                val correct = userAns.trim().equals(currentQuestion.correctAnswer.trim(), ignoreCase = true)
                                isAnswerCorrect = correct
                                isAnswerChecked = true

                                if (correct) {
                                    correctCount++
                                    comboCount++
                                } else {
                                    comboCount = 0
                                    localHearts = maxOf(0, localHearts - 1)
                                    onUseHeart()
                                    if (localHearts <= 0) {
                                        showOutOfHeartsDialog = true
                                    }
                                }
                            },
                            enabled = if (currentQuestion.type == QuizType.SENTENCE_ARRANGEMENT) placedWords.isNotEmpty() else selectedAnswer != null,
                            faceColor = DuoGreen,
                            shadowColor = DuoGreenDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Duolingo Slide-Up Feedback Bottom Sheet
                AnimatedVisibility(
                    visible = isAnswerChecked,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it },
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    DuoFeedbackBanner(
                        isCorrect = isAnswerCorrect,
                        explanation = currentQuestion.correctAnswer + "\n" + currentQuestion.explanation,
                        onContinue = {
                            if (currentQuestionIndex + 1 < quizData.questions.size) {
                                currentQuestionIndex++
                            } else {
                                isQuizCompleted = true
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DuoCelebrationScreen(
    title: String,
    totalQuestions: Int,
    correctCount: Int,
    xpReward: Int,
    combo: Int,
    onFinish: (stars: Int, score: Int) -> Unit,
    onClose: () -> Unit
) {
    val accuracy = (correctCount.toFloat() / totalQuestions.toFloat() * 100).toInt()
    val stars = when {
        accuracy >= 90 -> 3
        accuracy >= 60 -> 2
        accuracy >= 40 -> 1
        else -> 0
    }
    val score = correctCount * 25

    LaunchedEffect(Unit) {
        onFinish(stars, score)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🦉", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = if (stars > 0) "LESSON COMPLETE!" else "PRACTICE COMPLETED",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DuoGreen
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Stars
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { idx ->
                val isEarned = idx < stars
                Text(
                    text = if (isEarned) "⭐" else "☆",
                    fontSize = 44.sp,
                    color = if (isEarned) DuoGold else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Rewards Strip
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "+$xpReward XP", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = DuoGoldDark)
                    Text(text = "Total XP", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$accuracy%", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = DuoGreen)
                    Text(text = "Accuracy", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "+15 💎", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = DuoBlue)
                    Text(text = "Gems Earned", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        DuoTactileButton(
            text = "CONTINUE ➡️",
            onClick = onClose,
            faceColor = DuoGreen,
            shadowColor = DuoGreenDark,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
