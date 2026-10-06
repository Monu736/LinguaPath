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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LifestyleCategory
import com.example.data.model.SupportedLanguage
import com.example.data.model.VocabularyItem
import com.example.ui.components.AudioButton
import com.example.ui.components.DuoLeaguesDialog
import com.example.ui.components.DuoShopDialog
import com.example.ui.components.DuoTactileButton
import com.example.ui.components.DuoTopBar
import com.example.ui.components.ProfileLoginDialog
import com.example.ui.components.SaveBookmarkButton
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.DuoBlue
import com.example.ui.theme.DuoBlueDark
import com.example.ui.theme.DuoGold
import com.example.ui.theme.DuoGoldDark
import com.example.ui.theme.DuoGreen
import com.example.ui.theme.DuoGreenDark
import com.example.ui.theme.DuoPurple
import com.example.ui.theme.DuoPurpleDark
import com.example.ui.theme.DuoRed
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseAccent
import com.example.ui.viewmodel.TrainerUiState

@Composable
fun HomeScreen(
    uiState: TrainerUiState,
    onLanguageSelected: (SupportedLanguage) -> Unit,
    onSpeak: (String, String) -> Unit,
    onToggleSaveWord: (Int) -> Unit,
    onNavigateToQuizArena: (LifestyleCategory?) -> Unit,
    onNavigateToLiveAiAgent: () -> Unit,
    onNavigateToPractice: () -> Unit,
    onBuyRefillHearts: () -> Unit,
    onBuyStreakFreeze: () -> Unit,
    onSaveProfileDetails: (name: String, email: String, phone: String, avatar: String) -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLang = uiState.currentLanguage
    val profile = uiState.userProfile
    val wotd = uiState.wordOfTheDay
    val challenge = uiState.dailyChallenge

    var isProfileDialogOpen by remember { mutableStateOf(false) }
    var isShopDialogOpen by remember { mutableStateOf(false) }
    var isLeaguesDialogOpen by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_screen_content"),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // DUOLINGO TOP STATS BAR (Language, Streak 🔥, Gems 💎, Hearts ❤️ + Profile Avatar)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
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
                    }

                    // Profile Icon Button in top right
                    Surface(
                        shape = CircleShape,
                        color = DuoBlue.copy(alpha = 0.15f),
                        border = BorderStroke(2.dp, DuoBlue),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(42.dp)
                            .clickable { isProfileDialogOpen = true }
                            .testTag("home_profile_icon_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = profile?.avatarEmoji ?: "⚡",
                                fontSize = 22.sp
                            )
                        }
                    }
                }
            }

            // User Greeting & Saved Credentials Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🧭 Linguapath",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black,
                                    color = DuoGreen
                                )
                                Text(
                                    text = " • 1000 Levels",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Hello, ${profile?.userName ?: "Monu"}! 👋",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "📧 ${profile?.userEmail ?: "monugupta7478@gmail.com"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = DuoBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "📱 ${profile?.userPhone ?: "+91 98765 43210"} • Saved Profile",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DuoBlue.copy(alpha = 0.1f),
                            modifier = Modifier.clickable { isProfileDialogOpen = true }
                        ) {
                            Text(
                                text = "Edit Profile ✏️",
                                style = MaterialTheme.typography.labelMedium,
                                color = DuoBlue,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // DUOLINGO LEADERBOARD STRIP (RUBY LEAGUE)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DuoPurple.copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, DuoPurple.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { isLeaguesDialogOpen = true }
                        .testTag("home_leagues_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🏆", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Ruby League • Rank #4",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = DuoPurple
                                )
                                Text(
                                    text = "Top 5 promote to Diamond League",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DuoTactileButton(
                            text = "LEADERBOARD",
                            onClick = { isLeaguesDialogOpen = true },
                            faceColor = DuoPurple,
                            shadowColor = DuoPurpleDark,
                            height = 36.dp,
                            bevelDepth = 3.dp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // LIVE AI TALKING AGENT HERO CARD (WITH TACTILE BUTTON)
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = DuoGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("home_live_ai_agent_banner")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(DuoGreen, Color(0xFF46A302))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🦉", fontSize = 28.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Live AI Language Partner",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Speaks back live with native voice!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "AUDIO LIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Talk live with mic in ${currentLang.displayName}! The AI agent listens and literally answers back out loud with corrections and tips.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.95f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tactile Physical 3D Button
                        DuoTactileButton(
                            text = "START VOICE CONVERSATION 🎙️",
                            onClick = onNavigateToLiveAiAgent,
                            faceColor = Color.White,
                            shadowColor = Color(0xFFD4D4D4),
                            textColor = DuoGreenDark,
                            height = 50.dp,
                            bevelDepth = 4.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // 100-LEVEL QUIZ ARENA (10 LIFESTYLE CATEGORIES)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🎯 100-Level Quiz Arena",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "10 Lifestyle categories • 1,000 total levels",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DuoTactileButton(
                        text = "EXPLORE 100",
                        onClick = { onNavigateToQuizArena(null) },
                        faceColor = DuoBlue,
                        shadowColor = DuoBlueDark,
                        height = 38.dp,
                        bevelDepth = 3.dp
                    )
                }

                // Lifestyle Categories Carousel
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(LifestyleCategory.entries) { cat ->
                        LifestyleCategoryCard(
                            category = cat,
                            onClick = { onNavigateToQuizArena(cat) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Word of the Day with Audio
            if (wotd != null) {
                item {
                    Text(
                        text = "📖 Word of the Day",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = wotd.word,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = DuoBlue
                                    )
                                    if (wotd.transliteration.isNotBlank()) {
                                        Text(
                                            text = wotd.transliteration,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row {
                                    AudioButton(onClick = { onSpeak(wotd.word, currentLang.code) })
                                    Spacer(modifier = Modifier.width(4.dp))
                                    SaveBookmarkButton(
                                        isSaved = wotd.isSaved,
                                        onToggle = { onToggleSaveWord(wotd.id) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Meaning: ${wotd.meaning}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )

                            if (wotd.exampleSentence.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.background,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "“${wotd.exampleSentence}”",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (wotd.exampleTranslation.isNotBlank()) {
                                            Text(
                                                text = wotd.exampleTranslation,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Daily Quests Strip (Like Duolingo Quests)
            if (challenge != null) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DuoGold.copy(alpha = 0.12f)),
                        border = BorderStroke(1.dp, DuoGold.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "📜 Daily Quest Progress", fontWeight = FontWeight.ExtraBold, color = DuoGoldDark)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "+50 XP • +10 💎", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DuoBlue)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { 0.70f },
                                    color = DuoGold,
                                    trackColor = Color(0xFFE5E5E5),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Complete 3 levels in Food or Gaming (2/3 done)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Profile & Login Manager Dialog
        if (isProfileDialogOpen) {
            ProfileLoginDialog(
                userProfile = profile,
                onSaveProfile = { name, email, phone, avatar ->
                    onSaveProfileDetails(name, email, phone, avatar)
                    isProfileDialogOpen = false
                },
                onDismiss = { isProfileDialogOpen = false }
            )
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

        // Duolingo Leagues Dialog
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

@Composable
fun LifestyleCategoryCard(
    category: LifestyleCategory,
    onClick: () -> Unit
) {
    val accentColor = Color(category.accentHex)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() }
            .testTag("home_category_card_${category.key}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = category.emoji, fontSize = 28.sp)
                if (category.isTrending) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = accentColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "HOT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = category.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "100 Progressive Levels",
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.tagline,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}
