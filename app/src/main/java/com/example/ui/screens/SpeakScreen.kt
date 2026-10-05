package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.ConversationScenario
import com.example.data.model.DialogueTurn
import com.example.data.model.SpeakingEvaluation
import com.example.data.model.SpeakingPrompt
import com.example.data.model.SupportedLanguage
import com.example.trainer.PersonalizationEngine
import com.example.ui.components.AudioButton
import com.example.ui.components.LanguageHeader
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseAccent
import com.example.ui.viewmodel.TrainerUiState

@Composable
fun SpeakScreen(
    uiState: TrainerUiState,
    onLanguageSelected: (SupportedLanguage) -> Unit,
    onSpeak: (String, String) -> Unit,
    onStartListeningMic: (SpeakingPrompt, (SpeakingEvaluation) -> Unit, (String) -> Unit) -> Unit,
    onStopListeningMic: () -> Unit,
    onStartScenario: (ConversationScenario) -> Unit,
    onSendScenarioMessage: (String) -> Unit,
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    val currentLang = uiState.currentLanguage
    val profile = uiState.userProfile
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }
    val tabs = listOf("🗣️ Speaking Coach", "🤖 AI Conversation Partner")

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
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> SpeakingCoachTab(
                languageCode = currentLang.code,
                isListeningToMic = uiState.isListeningToMic,
                lastEvaluation = uiState.lastSpeakingEvaluation,
                onSpeak = onSpeak,
                onStartMic = onStartListeningMic,
                onStopMic = onStopListeningMic
            )
            1 -> AiConversationPartnerTab(
                languageCode = currentLang.code,
                activeScenario = uiState.activeScenario,
                turns = uiState.scenarioTurns,
                isAiThinking = uiState.isAiThinking,
                onSpeak = onSpeak,
                onSelectScenario = onStartScenario,
                onSendMessage = onSendScenarioMessage
            )
        }
    }
}

@Composable
private fun SpeakingCoachTab(
    languageCode: String,
    isListeningToMic: Boolean,
    lastEvaluation: SpeakingEvaluation?,
    onSpeak: (String, String) -> Unit,
    onStartMic: (SpeakingPrompt, (SpeakingEvaluation) -> Unit, (String) -> Unit) -> Unit,
    onStopMic: () -> Unit
) {
    val context = LocalContext.current
    val prompts = remember(languageCode) { PersonalizationEngine.getSpeakingPrompts(languageCode) }
    var promptIndex by remember(languageCode) { mutableIntStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val currentPrompt = prompts.getOrNull(promptIndex) ?: return

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            errorMessage = null
            onStartMic(currentPrompt, {}, { errorMessage = it })
        } else {
            errorMessage = "Microphone permission is required to analyze speech pronunciation."
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Speaking & Pronunciation Coach",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Listen to the native pronunciation, then tap the mic and speak the sentence aloud.",
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Prompt ${promptIndex + 1} of ${prompts.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        AudioButton(onClick = { onSpeak(currentPrompt.sentence, currentPrompt.languageCode) })
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = currentPrompt.sentence,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    if (currentPrompt.transliteration != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentPrompt.transliteration,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"${currentPrompt.translation}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "💡 Tip: ${currentPrompt.tips}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Big Mic Recording Button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(80.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (isListeningToMic) {
                                    onStopMic()
                                } else {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasPermission) {
                                        errorMessage = null
                                        onStartMic(currentPrompt, {}, { errorMessage = it })
                                    } else {
                                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(if (isListeningToMic) RoseAccent else IndigoPrimary)
                                .testTag("speaking_mic_button")
                        ) {
                            Icon(
                                imageVector = if (isListeningToMic) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Record speech",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isListeningToMic) "Listening... Speak clearly now" else "Tap Mic to Speak",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isListeningToMic) RoseAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (errorMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        // Evaluation Result
        if (lastEvaluation != null) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, EmeraldSecondary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Speech Analysis Results",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldSecondary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${lastEvaluation.pronunciationScore}% Pronunciation",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "You said: \"${lastEvaluation.recognizedText.ifEmpty { "[No speech recognized]" }}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ScorePill("Word Accuracy", "${lastEvaluation.accuracyScore}%")
                            ScorePill("Pronunciation", "${lastEvaluation.pronunciationScore}%")
                            ScorePill("Estimated Speed", "${lastEvaluation.speedWpm} WPM")
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Coach Feedback: ${lastEvaluation.feedback}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                promptIndex = (promptIndex + 1) % prompts.size
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                        ) {
                            Text("Next Sentence → (+15 XP)")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScorePill(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = IndigoPrimary)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AiConversationPartnerTab(
    languageCode: String,
    activeScenario: ConversationScenario?,
    turns: List<DialogueTurn>,
    isAiThinking: Boolean,
    onSpeak: (String, String) -> Unit,
    onSelectScenario: (ConversationScenario) -> Unit,
    onSendMessage: (String) -> Unit
) {
    val scenarios = remember(languageCode) { PersonalizationEngine.getConversationScenarios(languageCode) }
    var userTypedInput by remember { mutableStateOf("") }

    if (activeScenario == null) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "AI Conversation Scenarios",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Practice realistic two-way roleplays. The AI responds naturally and provides instant suggestions to elevate your grammar and vocabulary.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(scenarios) { scenario ->
                Card(
                    onClick = { onSelectScenario(scenario) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = scenario.iconEmoji, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = scenario.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = IndigoPrimary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = scenario.level,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = scenario.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Active Scenario Dialogue View
        Column(modifier = Modifier.fillMaxSize()) {
            // Scenario Subheader
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = activeScenario.iconEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = activeScenario.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Level: ${activeScenario.level}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Message Bubble History
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(turns) { turn ->
                    val isAi = turn.sender == "AI"
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (isAi) Alignment.Start else Alignment.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isAi) 2.dp else 16.dp,
                                bottomEnd = if (isAi) 16.dp else 2.dp
                            ),
                            color = if (isAi) MaterialTheme.colorScheme.surfaceVariant else IndigoPrimary,
                            border = if (isAi) BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) else null,
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = turn.text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isAi) MaterialTheme.colorScheme.onSurface else Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isAi) {
                                        AudioButton(
                                            onClick = { onSpeak(turn.text, activeScenario.languageCode) },
                                            sizeDp = 32
                                        )
                                    }
                                }

                                if (turn.transliteration != null && isAi) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = turn.transliteration,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }

                                if (turn.translation.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = turn.translation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isAi) MaterialTheme.colorScheme.onSurfaceVariant else Color.White.copy(alpha = 0.8f)
                                    )
                                }

                                if (turn.betterAlternative != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = EmeraldSecondary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "💡 More natural: \"${turn.betterAlternative}\"",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = EmeraldSecondary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (isAiThinking) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "AI partner is typing...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Quick reply suggestion chips
            if (activeScenario.sampleUserReplies.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    activeScenario.sampleUserReplies.take(2).forEach { reply ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier.clickable {
                                onSendMessage(reply)
                            }
                        ) {
                            Text(
                                text = "💬 $reply",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Input bar
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userTypedInput,
                        onValueChange = { userTypedInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Reply in ${activeScenario.languageCode}...") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (userTypedInput.isNotBlank()) {
                                onSendMessage(userTypedInput)
                                userTypedInput = ""
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(IndigoPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
