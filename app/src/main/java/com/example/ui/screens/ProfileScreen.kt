package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SupportedLanguage
import com.example.ui.components.LanguageHeader
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.TrainerUiState

@Composable
fun ProfileScreen(
    uiState: TrainerUiState,
    onLanguageSelected: (SupportedLanguage) -> Unit,
    onSaveProfileDetails: (String, String, String, String) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onResetOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLang = uiState.currentLanguage
    val profile = uiState.userProfile
    val progress = uiState.languageProgress
    val achievements = uiState.achievements
    val vocabulary = uiState.vocabulary

    var isEditDialogOpen by remember { mutableStateOf(false) }

    val masteredCount = vocabulary.count { it.memoryState == "MASTERED" }
    val familiarCount = vocabulary.count { it.memoryState == "FAMILIAR" }
    val learningCount = vocabulary.count { it.memoryState == "LEARNING" }
    val newCount = vocabulary.count { it.memoryState == "NEW" }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("profile_screen_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                LanguageHeader(
                    currentLanguage = currentLang,
                    streak = profile?.streak ?: 1,
                    xp = profile?.xp ?: 0,
                    onLanguageSelected = onLanguageSelected
                )
            }

            // Profile Avatar Card with User Name, Email and Phone details
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = IndigoPrimary.copy(alpha = 0.15f),
                            border = BorderStroke(2.dp, IndigoPrimary),
                            modifier = Modifier
                                .size(64.dp)
                                .clickable { isEditDialogOpen = true }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = profile?.avatarEmoji ?: "⚡", fontSize = 32.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = profile?.userName ?: "Monu Gupta",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                OutlinedButton(
                                    onClick = { isEditDialogOpen = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Edit ✏️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "📧 ${profile?.userEmail ?: "monugupta7478@gmail.com"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = IndigoPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "📱 ${profile?.userPhone ?: "+91 98765 43210"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldSecondary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Account Logged In • Data Saved Locally",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

        // High Level Stats Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatCard("🔥 Streak", "${profile?.streak ?: 1} Days", AmberTertiary, Modifier.weight(1f))
                ProfileStatCard("⭐ Total XP", "${profile?.xp ?: 0}", IndigoPrimary, Modifier.weight(1f))
                ProfileStatCard("🎯 Accuracy", "${progress?.accuracy ?: 86}%", EmeraldSecondary, Modifier.weight(1f))
            }
        }

        // Spaced Repetition Memory Distribution
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🧠 Memory Spaced Repetition Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Track how words transition from short-term memory to permanent mastery.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MemoryCountPill("NEW", newCount, Color.Gray)
                        MemoryCountPill("LEARNING", learningCount, AmberTertiary)
                        MemoryCountPill("FAMILIAR", familiarCount, IndigoPrimary)
                        MemoryCountPill("MASTERED", masteredCount, EmeraldSecondary)
                    }
                }
            }
        }

        // 6-Skill Progress Bars
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📊 6-Skill Proficiency Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    SkillProgressBar("📚 Vocabulary Power", (progress?.wordsLearned ?: 12) * 4, 100)
                    SkillProgressBar("✏️ Grammar Accuracy", (progress?.lessonsCompleted ?: 4) * 15, 100)
                    SkillProgressBar("🗣️ Speaking & Articulation", progress?.speakingScore ?: 80, 100)
                    SkillProgressBar("🎧 Listening Comprehension", progress?.listeningScore ?: 82, 100)
                    SkillProgressBar("📖 Reading Fluency", progress?.readingScore ?: 85, 100)
                    SkillProgressBar("✍️ Writing Precision", progress?.writingScore ?: 78, 100)
                }
            }
        }

        // Achievements Gallery
        item {
            Column {
                Text(
                    text = "🏆 Achievements & Badges",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                achievements.forEach { achievement ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (achievement.isUnlocked) EmeraldSecondary.copy(alpha = 0.08f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (achievement.isUnlocked) EmeraldSecondary.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = achievement.iconEmoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = achievement.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = achievement.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (achievement.isUnlocked) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldSecondary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "UNLOCKED",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Settings
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚙️ Preferences & Training Goals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Daily Reminder Notifications", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text(text = "Maintain your learning streak", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = profile?.notificationsEnabled ?: true,
                            onCheckedChange = onToggleNotifications,
                            colors = SwitchDefaults.colors(checkedThumbColor = IndigoPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = onResetOnboarding,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Recalibrate Goals & Take Placement Survey")
                    }
                }
            }
        }
    }

    if (isEditDialogOpen) {
        com.example.ui.components.ProfileLoginDialog(
            userProfile = profile,
            onSaveProfile = { name, email, phone, avatar ->
                onSaveProfileDetails(name, email, phone, avatar)
                isEditDialogOpen = false
            },
            onDismiss = { isEditDialogOpen = false }
        )
    }
}
}

@Composable
private fun ProfileStatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MemoryCountPill(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.15f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "$count", fontWeight = FontWeight.Bold, color = color)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
private fun SkillProgressBar(name: String, current: Int, max: Int) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Text(text = "$current%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (current.toFloat() / max.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = IndigoPrimary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
