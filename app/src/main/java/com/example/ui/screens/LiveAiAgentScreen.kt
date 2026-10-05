package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.DialogueTurn
import com.example.data.model.SupportedLanguage
import com.example.ui.components.LanguageHeader
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseAccent
import com.example.ui.viewmodel.TrainerUiState

@Composable
fun LiveAiAgentScreen(
    uiState: TrainerUiState,
    onLanguageSelected: (SupportedLanguage) -> Unit,
    onSendMessage: (String) -> Unit,
    onStartMicListening: (onError: (String) -> Unit) -> Unit,
    onStopMicListening: () -> Unit,
    onSpeak: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLang = uiState.currentLanguage
    val profile = uiState.userProfile
    val isSpeaking = uiState.isLiveAiSpeaking
    val isListening = uiState.isListeningToMic
    val isAiThinking = uiState.isAiThinking
    val history = uiState.liveAiDialogueHistory
    val statusText = uiState.liveAiStatusText

    val context = LocalContext.current
    var inputMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var slowAudioMode by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(history.size, isAiThinking) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    // Permission launcher for live speech input
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onStartMicListening { err -> errorMessage = err }
        } else {
            errorMessage = "Microphone permission is required for live voice conversation."
        }
    }

    // Animated pulsing effect for the AI Voice Avatar
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isSpeaking || isListening) 1.15f else 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isListening) 600 else 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("live_ai_agent_screen")
    ) {
        // Top Header
        LanguageHeader(
            currentLanguage = currentLang,
            streak = profile?.streak ?: 3,
            xp = profile?.xp ?: 240,
            onLanguageSelected = onLanguageSelected
        )

        // Live AI Status Banner with animated avatar
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pulsing Agent Avatar
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(IndigoPrimary, EmeraldSecondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSpeaking) "🗣️" else if (isListening) "🎙️" else "🤖",
                        fontSize = 26.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Live AI Language Partner",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSpeaking) EmeraldSecondary.copy(alpha = 0.2f) else IndigoPrimary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (isSpeaking) "SPEAKING LIVE" else if (isListening) "LISTENING" else "ONLINE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSpeaking) EmeraldSecondary else IndigoPrimary,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Quick Topic Chips (Lifestyle & Trending)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val quickTopics = when (currentLang) {
                SupportedLanguage.HINDI -> listOf(
                    "🍕 स्ट्रीट फ़ूड का ऑर्डर कैसे करें?" to "भैया, दो प्लेट समोसे और गर्म चाय देना।",
                    "🎮 गेमिंग में टीम से कैसे बात करें?" to "छत पर बंदा है, मुझे कवर दो!",
                    "🎒 कॉलेज कैंटीन में बात करें" to "आज क्लास के बाद कैंटीन चलें?",
                    "✈️ एयरपोर्ट पर हिंदी में बात करें" to "माफ़ कीजिए, सुरक्षा जांच कहाँ है?",
                    "💬 हाल-चाल पूछें" to "नमस्ते! आपका आज का दिन कैसा रहा?"
                )
                SupportedLanguage.MARATHI -> listOf(
                    "🍕 मिसळ पाव ऑर्डर करा" to "एक प्लेट झणझणीत मिसळ पाव द्या.",
                    "🎮 मित्रांसोबत गेम खेळा" to "आपण हा सामना नक्की जिंकू!",
                    "💬 आजचा दिवस कसा होता?" to "नमस्कार! तुमचा आजचा दिवस कसा गेला?",
                    "✈️ रेल्वे स्थानक कुठे आहे?" to "माफ करा, छत्रपती शिवाजी महाराज टर्मिनस कुठे आहे?"
                )
                SupportedLanguage.JAPANESE -> listOf(
                    "🍕 レストランで注文" to "すみません、おすすめは何ですか？ (Excuse me, what do you recommend?)",
                    "🎮 ゲームの会話" to "一緒にゲームをしましょう！ (Let's play games together!)",
                    "💬 自己紹介" to "初めまして！どうぞよろしくお願いします。 (Nice to meet you!)",
                    "✈️ 道を聞く" to "駅はどこですか？ (Where is the station?)"
                )
                else -> listOf(
                    "🍕 Order at a fancy café" to "Could I please get a large iced matcha latte?",
                    "🎮 Discord gaming comms" to "Need backup at point B, enemy team pushing!",
                    "📱 Explain trending Gen-Z slang" to "Can you teach me how to use 'no cap' and 'rizz'?",
                    "💼 Job interview prep" to "Tell me about a time you solved a tough problem.",
                    "✈️ Airport luggage inquiry" to "Where can I find the baggage claim for flight 204?"
                )
            }

            items(quickTopics) { (chipLabel, promptText) ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.clickable {
                        onSendMessage(promptText)
                    }
                ) {
                    Text(
                        text = chipLabel,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Conversation Chat Timeline
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("ai_conversation_timeline")
        ) {
            items(history) { turn ->
                DialogueTurnBubble(
                    turn = turn,
                    currentLanguage = currentLang,
                    onSpeak = { text -> onSpeak(text, currentLang.code) }
                )
            }

            if (isAiThinking) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = IndigoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Coach is typing & formulating native speech...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Error message notification
        errorMessage?.let { err ->
            Surface(
                color = RoseAccent.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = err,
                    color = RoseAccent,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // Bottom Live Input & Voice Bar
        Surface(
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Big Live Mic Button (Talks live)
                IconButton(
                    onClick = {
                        if (isListening) {
                            onStopMicListening()
                        } else {
                            val permissionCheck = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            )
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                onStartMicListening { err -> errorMessage = err }
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isListening) RoseAccent else IndigoPrimary)
                        .testTag("ai_live_mic_button")
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Speak Live",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Text Field
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    placeholder = {
                        Text(
                            text = "Speak live with mic or type in ${currentLang.displayName}...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_message_text_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send Button
                IconButton(
                    onClick = {
                        if (inputMessage.isNotBlank()) {
                            onSendMessage(inputMessage.trim())
                            inputMessage = ""
                        }
                    },
                    enabled = inputMessage.isNotBlank(),
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (inputMessage.isNotBlank()) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("ai_send_message_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputMessage.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun DialogueTurnBubble(
    turn: DialogueTurn,
    currentLanguage: SupportedLanguage,
    onSpeak: (String) -> Unit
) {
    val isUser = turn.sender == "USER"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(IndigoPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🤖", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Main Spoken Text
                Text(
                    text = turn.text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                )

                // Transliteration (Devanagari / Romaji)
                if (!turn.transliteration.isNullOrBlank() && !isUser) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = turn.transliteration,
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Translation
                if (turn.translation.isNotBlank() && !isUser) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = turn.translation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Audio Playback button for AI answers
                if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = IndigoPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.clickable { onSpeak(turn.text) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Listen again",
                                    tint = IndigoPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Listen",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoPrimary
                                )
                            }
                        }
                    }
                }

                // Real-time Grammar Correction / Advice (if any)
                if (!turn.grammarCorrection.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AmberTertiary.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "💡 Tip: ${turn.grammarCorrection}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AmberTertiary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                // Better natural phrasing suggestion
                if (!turn.betterAlternative.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldSecondary.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "✨ Native phrasing: ${turn.betterAlternative}",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldSecondary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
