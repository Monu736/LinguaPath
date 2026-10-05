package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SupportedLanguage
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.IndigoPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: (
        targetLanguage: SupportedLanguage,
        level: String,
        nativeLanguage: String,
        reasons: List<String>,
        focusAreas: List<String>,
        dailyGoalMinutes: Int
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 7

    var selectedLanguage by remember { mutableStateOf(SupportedLanguage.ENGLISH) }
    var selectedLevel by remember { mutableStateOf("Beginner") }
    var selectedNativeLang by remember { mutableStateOf("Hindi") }
    val selectedReasons = remember { mutableStateListOf("Speaking & Fluency", "Job & Career") }
    val selectedFocusAreas = remember { mutableStateListOf("Vocabulary", "Grammar", "Speaking") }
    var selectedDailyGoal by remember { mutableIntStateOf(15) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Personal Language Trainer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Step $currentStep of $totalSteps",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    if (currentStep > 1) {
                        IconButton(onClick = { currentStep-- }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous step"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { currentStep.toFloat() / totalSteps.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = IndigoPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (currentStep < totalSteps) {
                                currentStep++
                            } else {
                                onFinishOnboarding(
                                    selectedLanguage,
                                    selectedLevel,
                                    selectedNativeLang,
                                    selectedReasons.toList(),
                                    selectedFocusAreas.toList(),
                                    selectedDailyGoal
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("onboarding_next_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (currentStep == totalSteps) "Start Learning Now 🚀" else "Continue",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }
                },
                label = "onboarding_step"
            ) { step ->
                when (step) {
                    1 -> StepLanguage(
                        selected = selectedLanguage,
                        onSelect = { selectedLanguage = it }
                    )
                    2 -> StepLevel(
                        selected = selectedLevel,
                        onSelect = { selectedLevel = it }
                    )
                    3 -> StepNativeLanguage(
                        selected = selectedNativeLang,
                        onSelect = { selectedNativeLang = it }
                    )
                    4 -> StepReasons(
                        selectedList = selectedReasons,
                        onToggle = {
                            if (selectedReasons.contains(it)) selectedReasons.remove(it)
                            else selectedReasons.add(it)
                        }
                    )
                    5 -> StepFocusAreas(
                        selectedList = selectedFocusAreas,
                        onToggle = {
                            if (selectedFocusAreas.contains(it)) selectedFocusAreas.remove(it)
                            else selectedFocusAreas.add(it)
                        }
                    )
                    6 -> StepDailyGoal(
                        selected = selectedDailyGoal,
                        onSelect = { selectedDailyGoal = it }
                    )
                    7 -> StepSummary(
                        language = selectedLanguage,
                        level = selectedLevel,
                        nativeLanguage = selectedNativeLang,
                        dailyGoalMinutes = selectedDailyGoal,
                        reasons = selectedReasons,
                        focus = selectedFocusAreas
                    )
                }
            }
        }
    }
}

@Composable
private fun StepLanguage(
    selected: SupportedLanguage,
    onSelect: (SupportedLanguage) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        item {
            Text(
                text = "Which language do you want to learn?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "English and Hindi are our primary comprehensive courses. Marathi and Japanese are structured side courses.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        items(SupportedLanguage.entries) { lang ->
            val isSelected = lang == selected
            Card(
                onClick = { onSelect(lang) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) IndigoPrimary.copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("lang_option_${lang.code}")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = lang.flag, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = lang.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (lang.isPrimary) EmeraldSecondary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = if (lang.isPrimary) "PRIMARY" else "SIDE COURSE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (lang.isPrimary) EmeraldSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${lang.nativeName} • ${lang.scriptName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = lang.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }

                    if (isSelected) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = IndigoPrimary,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepLevel(
    selected: String,
    onSelect: (String) -> Unit
) {
    val levels = listOf(
        "Complete Beginner" to "Starting from zero, alphabet/script, and basic sounds",
        "Beginner" to "Know a few common words and basic greetings",
        "Elementary" to "Can construct simple sentences and understand basic dialogue",
        "Intermediate" to "Can have everyday conversations, need better grammar and vocabulary",
        "Upper Intermediate" to "Fluent in most topics, working on nuance and error-free expression",
        "Advanced" to "Refining professional fluency, idiomatic mastery, and pronunciation"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        item {
            Text(
                text = "What is your current level?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "We will tailor lessons, quizzes, and conversations to match your comfort zone.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        items(levels) { (title, desc) ->
            val isSelected = title == selected
            Card(
                onClick = { onSelect(title) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) IndigoPrimary.copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = IndigoPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepNativeLanguage(
    selected: String,
    onSelect: (String) -> Unit
) {
    val languages = listOf("Hindi", "English", "Marathi", "Bengali", "Telugu", "Tamil", "Gujarati", "Kannada", "Malayalam", "Punjabi", "Other")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        item {
            Text(
                text = "What is your native language?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "We provide translations, pronunciation analogies, and grammar explanations tailored to your mother tongue.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        items(languages) { lang ->
            val isSelected = lang == selected
            OutlinedCard(
                onClick = { onSelect(lang) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isSelected) IndigoPrimary.copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = lang,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    if (isSelected) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = IndigoPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepReasons(
    selectedList: List<String>,
    onToggle: (String) -> Unit
) {
    val reasons = listOf(
        "🗣️ Speaking & Natural Fluency",
        "💼 Job, Career & Interviews",
        "✈️ Travel & Living Abroad",
        "🎓 School, College & Exams",
        "🤝 Talking with Friends & Family",
        "🧠 Brain Training & Personal Growth",
        "🎬 Understanding Movies & Media"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        item {
            Text(
                text = "Why are you learning?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Select all goals that apply so we prioritize relevant dialogue topics.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        items(reasons) { reason ->
            val isSelected = selectedList.contains(reason)
            Card(
                onClick = { onToggle(reason) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) IndigoPrimary.copy(alpha = 0.1f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                ),
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
                        text = reason,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    if (isSelected) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = IndigoPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepFocusAreas(
    selectedList: List<String>,
    onToggle: (String) -> Unit
) {
    val focusAreas = listOf(
        "📚 Vocabulary & Idioms" to "Build rich everyday & professional word power",
        "✏️ Grammar & Sentence Structure" to "Master tenses, gender agreement, and word order",
        "🗣️ Speaking & Pronunciation" to "AI voice coach with real-time articulation feedback",
        "🎧 Listening & Comprehension" to "Multiple audio speeds and real speech decoding",
        "📖 Reading & Comprehension" to "Passages with interactive word tap-to-inspect",
        "✍️ Writing & Error Correction" to "Real-time grammar mistake highlighting and explanations"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        item {
            Text(
                text = "What areas do you want to improve?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Select all skills you want our Personal Language Trainer to sharpen.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        items(focusAreas) { (title, subtitle) ->
            val isSelected = selectedList.contains(title)
            Card(
                onClick = { onToggle(title) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) IndigoPrimary.copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isSelected) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = IndigoPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepDailyGoal(
    selected: Int,
    onSelect: (Int) -> Unit
) {
    val goals = listOf(
        5 to ("Casual" to "5 minutes a day • 1 quick lesson & review"),
        10 to ("Regular" to "10 minutes a day • 1 lesson, vocabulary & quiz"),
        15 to ("Recommended" to "15 minutes a day • Balanced full cycle (Learn, Practice, Speak)"),
        30 to ("Serious" to "30 minutes a day • Rapid progress, deep grammar & conversation"),
        60 to ("Intensive" to "60 minutes a day • Maximum fluency in shortest time")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        item {
            Text(
                text = "Set your daily learning goal",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Consistency beats cramming. Just 15 minutes a day creates lifelong fluency.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        items(goals) { (mins, details) ->
            val (label, desc) = details
            val isSelected = mins == selected
            Card(
                onClick = { onSelect(mins) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) IndigoPrimary.copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$mins min / day",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (mins == 15) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldSecondary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "RECOMMENDED",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$label • $desc",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isSelected) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = IndigoPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepSummary(
    language: SupportedLanguage,
    level: String,
    nativeLanguage: String,
    dailyGoalMinutes: Int,
    reasons: List<String>,
    focus: List<String>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = EmeraldSecondary.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, EmeraldSecondary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎉", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Your Personalized Training Plan is Ready!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Course Configuration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SummaryItem("Target Language", "${language.flag} ${language.displayName} (${language.nativeName})")
                    SummaryItem("Current Level", level)
                    SummaryItem("Native Language", nativeLanguage)
                    SummaryItem("Daily Commitment", "$dailyGoalMinutes minutes / day")
                    SummaryItem("Focus Skills", if (focus.isEmpty()) "All Core Skills" else focus.take(3).joinToString(", "))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Your 7-Step Daily Learning Loop",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            val cycle = listOf(
                "1. LEARN" to "New vocabulary with native audio & phonetics",
                "2. UNDERSTAND" to "Grammar rules, sentence logic & common traps",
                "3. PRACTICE" to "Interactive adaptive quizzes & word arrangement",
                "4. REMEMBER" to "Spaced repetition flashcards (4 memory states)",
                "5. REVISE" to "Targeted review of your Mistake Notebook",
                "6. SPEAK" to "Mic voice practice with accuracy score & pronunciation feedback",
                "7. USE" to "AI conversation partner roleplay in realistic scenarios"
            )

            cycle.forEach { (stepTitle, stepDesc) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = IndigoPrimary.copy(alpha = 0.1f),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = stepTitle,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = stepDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
