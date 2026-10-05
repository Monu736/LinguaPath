package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ListeningExercise
import com.example.data.model.MistakeItem
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizType
import com.example.data.model.SupportedLanguage
import com.example.data.model.VocabularyItem
import com.example.data.model.WritingExercise
import com.example.data.model.WritingFeedback
import com.example.trainer.PersonalizationEngine
import com.example.ui.components.AudioButton
import com.example.ui.components.LanguageHeader
import com.example.ui.components.MemoryBadge
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseAccent
import com.example.ui.viewmodel.TrainerUiState

@Composable
fun PracticeScreen(
    uiState: TrainerUiState,
    onLanguageSelected: (SupportedLanguage) -> Unit,
    onSpeak: (String, String) -> Unit,
    onUpdateSpacedRepetition: (VocabularyItem, Boolean, Int) -> Unit,
    onResolveMistake: (Int) -> Unit,
    onRecordMistake: (category: String, prompt: String, userAns: String, correctAns: String, explanation: String) -> Unit,
    onEvaluateWriting: (WritingExercise, String) -> Unit,
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    val currentLang = uiState.currentLanguage
    val profile = uiState.userProfile
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }
    val tabs = listOf("🧠 My Mistakes", "⚡ Adaptive Quiz", "🗂️ Flashcards", "🎧 Listening Lab", "✍️ Writing")

    Column(modifier = modifier.fillMaxSize()) {
        LanguageHeader(
            currentLanguage = currentLang,
            streak = profile?.streak ?: 1,
            xp = profile?.xp ?: 0,
            onLanguageSelected = onLanguageSelected
        )

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                val badgeCount = if (index == 0) uiState.activeMistakes.size else null
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                            if (badgeCount != null && badgeCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = RoseAccent,
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$badgeCount",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> MistakesNotebookTab(
                mistakes = uiState.activeMistakes,
                onResolve = onResolveMistake
            )
            1 -> AdaptiveQuizTab(
                languageCode = currentLang.code,
                onSpeak = onSpeak,
                onRecordMistake = onRecordMistake
            )
            2 -> SmartFlashcardsTab(
                vocabulary = uiState.vocabulary,
                onSpeak = onSpeak,
                onFeedback = onUpdateSpacedRepetition
            )
            3 -> ListeningLabTab(
                languageCode = currentLang.code,
                onSpeak = onSpeak
            )
            4 -> WritingTrainerTab(
                languageCode = currentLang.code,
                feedback = uiState.writingFeedback,
                onEvaluate = onEvaluateWriting
            )
        }
    }
}

// ==========================================
// 1. MISTAKE NOTEBOOK TAB
// ==========================================
@Composable
private fun MistakesNotebookTab(
    mistakes: List<MistakeItem>,
    onResolve: (Int) -> Unit
) {
    if (mistakes.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🎉", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Mistake Notebook is Clean!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Any errors from quizzes, grammar, speaking, or writing will automatically be stored here for targeted practice.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Mistake Notebook (${mistakes.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Practicing your specific mistakes is the fastest path to natural fluency.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(mistakes) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, RoseAccent.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = RoseAccent.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = item.category.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = RoseAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Button(
                                onClick = { onResolve(item.id) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = "Mark Resolved (+10 XP)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = item.prompt,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "❌ Your answer: ${item.userAnswer}",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "✅ Correct answer: ${item.correctAnswer}",
                                    color = EmeraldSecondary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "💡 Explanation: ${item.explanation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. ADAPTIVE QUIZ TAB
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdaptiveQuizTab(
    languageCode: String,
    onSpeak: (String, String) -> Unit,
    onRecordMistake: (String, String, String, String, String) -> Unit
) {
    val questions = remember(languageCode) { PersonalizationEngine.getAdaptiveQuizzes(languageCode) }
    var currentIndex by remember(languageCode) { mutableIntStateOf(0) }
    var score by remember(languageCode) { mutableIntStateOf(0) }
    var selectedOption by remember(currentIndex) { mutableStateOf<String?>(null) }
    var isAnswerSubmitted by remember(currentIndex) { mutableStateOf(false) }

    // State for Sentence Arrangement
    val currentQuestion = questions.getOrNull(currentIndex)
    val arrangedWords = remember(currentIndex) { mutableStateListOf<String>() }

    // State for Word Matching
    val matchedPairs = remember(currentIndex) { mutableStateMapOf<String, String>() }
    var selectedLeftWord by remember(currentIndex) { mutableStateOf<String?>(null) }

    if (currentQuestion == null || currentIndex >= questions.size) {
        // Quiz Result Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🏆", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Quiz Completed!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Score: $score / ${questions.size}",
                        style = MaterialTheme.typography.titleLarge,
                        color = IndigoPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            currentIndex = 0
                            score = 0
                            selectedOption = null
                            isAnswerSubmitted = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Practice Again")
                    }
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question ${currentIndex + 1} of ${questions.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = IndigoPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = currentQuestion.type.name.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = IndigoPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = currentQuestion.question,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (currentQuestion.audioText != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AudioButton(
                        onClick = { onSpeak(currentQuestion.audioText, languageCode) },
                        sizeDp = 48
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Tap to listen to audio prompt", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Options according to QuizType
        when (currentQuestion.type) {
            QuizType.SENTENCE_ARRANGEMENT -> {
                item {
                    Text(text = "Constructed Sentence:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = if (arrangedWords.isEmpty()) "Tap words below to arrange sentence..." else arrangedWords.joinToString(" "),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = if (arrangedWords.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = "Available Words:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        currentQuestion.scrambledWords.forEach { word ->
                            val isUsed = arrangedWords.contains(word)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isUsed) Color.LightGray.copy(alpha = 0.3f) else IndigoPrimary.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, if (isUsed) Color.Transparent else IndigoPrimary),
                                modifier = Modifier
                                    .clickable(enabled = !isUsed && !isAnswerSubmitted) {
                                        arrangedWords.add(word)
                                    }
                            ) {
                                Text(
                                    text = word,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUsed) Color.Gray else IndigoPrimary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }

                    if (arrangedWords.isNotEmpty() && !isAnswerSubmitted) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { arrangedWords.clear() }) {
                            Text("Reset Arrangement")
                        }
                    }
                }
            }
            QuizType.WORD_MATCHING -> {
                item {
                    Text(
                        text = "Match each pair by tapping left then right:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    currentQuestion.correctPairs.forEach { (left, right) ->
                        val isMatched = matchedPairs[left] == right
                        Card(
                            onClick = {
                                if (!isMatched) {
                                    matchedPairs[left] = right
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isMatched) EmeraldSecondary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isMatched) EmeraldSecondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = left, fontWeight = FontWeight.Bold)
                                Text(text = if (isMatched) "↔ $right" else "[Tap to Match]", color = if (isMatched) EmeraldSecondary else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            QuizType.SPELLING -> {
                item {
                    var typedText by remember(currentIndex) { mutableStateOf("") }
                    OutlinedTextField(
                        value = typedText,
                        onValueChange = {
                            typedText = it
                            selectedOption = it
                        },
                        label = { Text("Type word here...") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isAnswerSubmitted
                    )
                }
            }
            else -> {
                items(currentQuestion.options) { option ->
                    val isSelected = selectedOption == option
                    val isCorrect = option == currentQuestion.correctAnswer

                    val cardColor = when {
                        !isAnswerSubmitted && isSelected -> IndigoPrimary.copy(alpha = 0.12f)
                        isAnswerSubmitted && isCorrect -> EmeraldSecondary.copy(alpha = 0.18f)
                        isAnswerSubmitted && isSelected && !isCorrect -> RoseAccent.copy(alpha = 0.18f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val borderColor = when {
                        !isAnswerSubmitted && isSelected -> IndigoPrimary
                        isAnswerSubmitted && isCorrect -> EmeraldSecondary
                        isAnswerSubmitted && isSelected && !isCorrect -> RoseAccent
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                    }

                    Card(
                        onClick = {
                            if (!isAnswerSubmitted) {
                                selectedOption = option
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        border = BorderStroke(if (isSelected || isAnswerSubmitted) 2.dp else 1.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isAnswerSubmitted && isCorrect) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSecondary)
                            } else if (isAnswerSubmitted && isSelected && !isCorrect) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = RoseAccent)
                            }
                        }
                    }
                }
            }
        }

        // Submit / Continue Button & Explanation
        item {
            Spacer(modifier = Modifier.height(18.dp))

            if (!isAnswerSubmitted) {
                Button(
                    onClick = {
                        isAnswerSubmitted = true
                        val isCorrect = when (currentQuestion.type) {
                            QuizType.SENTENCE_ARRANGEMENT -> arrangedWords.joinToString(" ").trim() == currentQuestion.targetSentence.trim()
                            QuizType.WORD_MATCHING -> matchedPairs.size == currentQuestion.correctPairs.size
                            QuizType.SPELLING -> selectedOption?.trim().equals(currentQuestion.correctAnswer.trim(), ignoreCase = true)
                            else -> selectedOption == currentQuestion.correctAnswer
                        }

                        if (isCorrect) {
                            score++
                        } else {
                            val userAns = when (currentQuestion.type) {
                                QuizType.SENTENCE_ARRANGEMENT -> arrangedWords.joinToString(" ")
                                QuizType.SPELLING -> selectedOption ?: ""
                                else -> selectedOption ?: "None"
                            }
                            onRecordMistake(
                                "Adaptive Quiz",
                                currentQuestion.question,
                                userAns,
                                currentQuestion.correctAnswer.ifEmpty { currentQuestion.targetSentence },
                                currentQuestion.explanation
                            )
                        }
                    },
                    enabled = when (currentQuestion.type) {
                        QuizType.SENTENCE_ARRANGEMENT -> arrangedWords.isNotEmpty()
                        QuizType.WORD_MATCHING -> matchedPairs.isNotEmpty()
                        else -> selectedOption != null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Check Answer", fontWeight = FontWeight.Bold)
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "💡 Explanation:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentQuestion.explanation,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        currentIndex++
                        selectedOption = null
                        isAnswerSubmitted = false
                        arrangedWords.clear()
                        matchedPairs.clear()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text(if (currentIndex + 1 < questions.size) "Next Question →" else "View Results", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// 3. SMART FLASHCARDS TAB
// ==========================================
@Composable
private fun SmartFlashcardsTab(
    vocabulary: List<VocabularyItem>,
    onSpeak: (String, String) -> Unit,
    onFeedback: (VocabularyItem, Boolean, Int) -> Unit
) {
    if (vocabulary.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No vocabulary cards available yet.")
        }
        return
    }

    var cardIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val currentCard = vocabulary[cardIndex.coerceIn(0, vocabulary.size - 1)]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Card ${cardIndex + 1} of ${vocabulary.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                MemoryBadge(state = currentCard.memoryState)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Flip Card Container
            Card(
                onClick = { isFlipped = !isFlipped },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(2.dp, IndigoPrimary.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .testTag("flashcard_flip_container")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isFlipped) {
                        // FRONT
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = currentCard.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = currentCard.word,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (currentCard.transliteration.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentCard.transliteration,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Pronunciation: /${currentCard.pronunciation}/",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AudioButton(onClick = { onSpeak(currentCard.word, currentCard.languageCode) })
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Tap card to flip ↺",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        // BACK
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Meaning",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentCard.meaning,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "\"${currentCard.exampleSentence}\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentCard.exampleTranslation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "How well do you remember this word?",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 4 Spaced Repetition Memory Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onFeedback(currentCard, false, 1)
                        isFlipped = false
                        cardIndex = (cardIndex + 1) % vocabulary.size
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoseAccent),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("❌", fontSize = 16.sp)
                        Text("Forgot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        onFeedback(currentCard, true, 1)
                        isFlipped = false
                        cardIndex = (cardIndex + 1) % vocabulary.size
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberTertiary),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😐", fontSize = 16.sp)
                        Text("Hard", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        onFeedback(currentCard, true, 2)
                        isFlipped = false
                        cardIndex = (cardIndex + 1) % vocabulary.size
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🙂", fontSize = 16.sp)
                        Text("Good", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        onFeedback(currentCard, true, 3)
                        isFlipped = false
                        cardIndex = (cardIndex + 1) % vocabulary.size
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔥", fontSize = 16.sp)
                        Text("Mastered", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. LISTENING LAB TAB
// ==========================================
@Composable
private fun ListeningLabTab(
    languageCode: String,
    onSpeak: (String, String) -> Unit
) {
    val exercises = PersonalizationEngine.getListeningExercises(languageCode)
    var selectedExerciseIndex by remember { mutableIntStateOf(0) }
    var showTranscript by remember { mutableStateOf(false) }
    var selectedSpeed by remember { mutableStateOf(1.0f) }

    val currentEx = exercises.getOrNull(selectedExerciseIndex) ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Listening Lab & Speed Training",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Train your ears to understand native audio at different tempos. Try answering before peeking at the transcript.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AudioButton(
                        onClick = { onSpeak(currentEx.speechText, currentEx.languageCode) },
                        sizeDp = 64
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Tap to Listen", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Speed Selection:", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0.7f to "0.7x Slow", 1.0f to "1.0x Normal", 1.25f to "1.25x Fast").forEach { (speed, label) ->
                            val isSelected = selectedSpeed == speed
                            OutlinedButton(
                                onClick = { selectedSpeed = speed },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) IndigoPrimary.copy(alpha = 0.12f) else Color.Transparent
                                )
                            ) {
                                Text(text = label, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { showTranscript = !showTranscript },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (showTranscript) "Hide Transcript" else "Peek Transcript 👁️")
                    }

                    if (showTranscript) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = currentEx.transcript, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = currentEx.translation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. WRITING TRAINER TAB
// ==========================================
@Composable
private fun WritingTrainerTab(
    languageCode: String,
    feedback: WritingFeedback?,
    onEvaluate: (WritingExercise, String) -> Unit
) {
    val exercises = PersonalizationEngine.getWritingExercises(languageCode)
    var selectedIndex by remember { mutableIntStateOf(0) }
    val exercise = exercises.getOrNull(selectedIndex) ?: return

    var userText by remember(selectedIndex) { mutableStateOf(exercise.starterText) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Writing & Sentence Trainer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Type your response. Our trainer checks your subject-verb agreement, tenses, and spelling in real time.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = exercise.prompt,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = exercise.instruction,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = userText,
                        onValueChange = { userText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = { Text("Type your sentence...") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onEvaluate(exercise, userText) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Text("Check Sentence & Errors", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Feedback Card
        if (feedback != null) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (feedback.detectedMistakes.isEmpty()) EmeraldSecondary.copy(alpha = 0.12f)
                        else RoseAccent.copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (feedback.detectedMistakes.isEmpty()) EmeraldSecondary else RoseAccent
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (feedback.detectedMistakes.isEmpty()) "✅ Perfect Syntax!" else "⚠️ Grammar Adjustments",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (feedback.detectedMistakes.isEmpty()) EmeraldSecondary else RoseAccent
                            )
                            Text(
                                text = "Score: ${feedback.score}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = feedback.generalFeedback, style = MaterialTheme.typography.bodyMedium)

                        if (feedback.detectedMistakes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            feedback.detectedMistakes.forEach { mistake ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "❌ ${mistake.flaggedText}  ➔  ✅ ${mistake.correction}", fontWeight = FontWeight.Bold, color = EmeraldSecondary)
                                        Text(text = mistake.reason, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "💡 Ideal Reference Answer: \"${exercise.sampleAnswer}\"",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
