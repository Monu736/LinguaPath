package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.SupportedLanguage
import com.example.data.model.UserProfile
import com.example.ui.theme.DuoBlue
import com.example.ui.theme.DuoBlueDark
import com.example.ui.theme.DuoGem
import com.example.ui.theme.DuoGold
import com.example.ui.theme.DuoGoldDark
import com.example.ui.theme.DuoGreen
import com.example.ui.theme.DuoGreenDark
import com.example.ui.theme.DuoPurple
import com.example.ui.theme.DuoPurpleDark
import com.example.ui.theme.DuoRed
import com.example.ui.theme.DuoRedDark

// ==========================================
// 1. TACTILE 3D DUOLINGO BUTTON (PHYSICAL PRESS TRANSITION)
// ==========================================
@Composable
fun DuoTactileButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    faceColor: Color = DuoGreen,
    shadowColor: Color = DuoGreenDark,
    textColor: Color = Color.White,
    icon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    height: Dp = 50.dp,
    bevelDepth: Dp = 4.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Tactile physical depression animation
    val animatedOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) bevelDepth else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_press_offset"
    )

    val actualFaceColor = if (enabled) faceColor else Color(0xFFE5E5E5)
    val actualShadowColor = if (enabled) shadowColor else Color(0xFFCECECE)
    val actualTextColor = if (enabled) textColor else Color(0xFFAFAFAF)

    Box(
        modifier = modifier
            .height(height)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .testTag("duo_tactile_button")
    ) {
        // Bottom 3D shadow layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = bevelDepth)
                .clip(RoundedCornerShape(16.dp))
                .background(actualShadowColor)
        )

        // Top interactive face that depresses when pressed
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bevelDepth)
                .offset(y = animatedOffset)
                .clip(RoundedCornerShape(16.dp))
                .background(actualFaceColor),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = actualTextColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// ==========================================
// 2. DUOLINGO INTERACTIVE QUIZ OPTION CARD WITH SELECTION TRANSITIONS
// ==========================================
enum class DuoOptionState {
    DEFAULT,
    SELECTED,
    CORRECT,
    WRONG
}

@Composable
fun DuoOptionCard(
    text: String,
    state: DuoOptionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingBadge: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedOffset by animateDpAsState(
        targetValue = if (isPressed) 3.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "opt_press"
    )

    val scale by animateFloatAsState(
        targetValue = if (state == DuoOptionState.SELECTED) 1.02f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "opt_scale"
    )

    val (faceColor, shadowColor, borderColor, textColor) = when (state) {
        DuoOptionState.SELECTED -> Quadruple(
            Color(0xFFDDF4FF),
            DuoBlueDark,
            DuoBlue,
            DuoBlueDark
        )
        DuoOptionState.CORRECT -> Quadruple(
            Color(0xFFD7FFB8),
            DuoGreenDark,
            DuoGreen,
            DuoGreenDark
        )
        DuoOptionState.WRONG -> Quadruple(
            Color(0xFFFFDFE0),
            DuoRedDark,
            DuoRed,
            DuoRedDark
        )
        DuoOptionState.DEFAULT -> Quadruple(
            MaterialTheme.colorScheme.surface,
            Color(0xFFE5E5E5),
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.onSurface
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("duo_option_$text")
    ) {
        // Bottom bevel shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(shadowColor)
        )

        // Top face
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .offset(y = animatedOffset)
                .clip(RoundedCornerShape(16.dp))
                .background(faceColor)
                .border(2.dp, borderColor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (leadingBadge != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = borderColor.copy(alpha = 0.2f),
                            modifier = Modifier.padding(end = 10.dp)
                        ) {
                            Text(
                                text = leadingBadge,
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (state != DuoOptionState.DEFAULT) FontWeight.Bold else FontWeight.Medium,
                        color = textColor
                    )
                }

                if (state == DuoOptionState.CORRECT) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(DuoGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                } else if (state == DuoOptionState.WRONG) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(DuoRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

// ==========================================
// 3. DUOLINGO TOP STATS BAR (HEARTS, GEMS, STREAK)
// ==========================================
@Composable
fun DuoTopBar(
    currentLanguage: SupportedLanguage,
    streak: Int,
    gems: Int,
    hearts: Int,
    onLanguageClick: () -> Unit,
    onStreakClick: () -> Unit,
    onGemsClick: () -> Unit,
    onHeartsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Flag pill
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .clickable { onLanguageClick() }
                .testTag("duo_flag_pill")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = currentLanguage.flag, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "▼", fontSize = 9.sp, color = Color.Gray)
            }
        }

        // Streak Fire 🔥
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.Transparent,
            modifier = Modifier
                .clickable { onStreakClick() }
                .testTag("duo_streak_pill")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🔥", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$streak",
                    fontWeight = FontWeight.ExtraBold,
                    color = DuoGold,
                    fontSize = 15.sp
                )
            }
        }

        // Gems Diamond 💎
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.Transparent,
            modifier = Modifier
                .clickable { onGemsClick() }
                .testTag("duo_gems_pill")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "💎", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$gems",
                    fontWeight = FontWeight.ExtraBold,
                    color = DuoBlue,
                    fontSize = 15.sp
                )
            }
        }

        // Hearts ❤️
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.Transparent,
            modifier = Modifier
                .clickable { onHeartsClick() }
                .testTag("duo_hearts_pill")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "❤️", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$hearts",
                    fontWeight = FontWeight.ExtraBold,
                    color = DuoRed,
                    fontSize = 15.sp
                )
            }
        }
    }
}

// ==========================================
// 4. DUOLINGO SLIDE-UP FEEDBACK BANNER (SHEET)
// ==========================================
@Composable
fun DuoFeedbackBanner(
    isCorrect: Boolean,
    explanation: String,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isCorrect) Color(0xFFD7FFB8) else Color(0xFFFFDFE0)
    val titleColor = if (isCorrect) DuoGreenDark else DuoRedDark

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 12.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("duo_feedback_banner")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isCorrect) DuoGreen else DuoRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isCorrect) "Amazing! Spot on! 🎉" else "Correct solution:",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = titleColor
                    )
                    if (!isCorrect) {
                        Text(
                            text = explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = DuoRedDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            DuoTactileButton(
                text = "CONTINUE ➡️",
                onClick = onContinue,
                faceColor = if (isCorrect) DuoGreen else DuoRed,
                shadowColor = if (isCorrect) DuoGreenDark else DuoRedDark,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ==========================================
// 5. DUOLINGO SHOP MODAL (STREAK FREEZE, HEARTS REFILL)
// ==========================================
@Composable
fun DuoShopDialog(
    userGems: Int,
    streakFreezes: Int,
    currentHearts: Int,
    onBuyRefill: () -> Unit,
    onBuyFreeze: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().testTag("duo_shop_dialog")
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
                    Text(text = "💎 Gem Shop", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "You have $userGems Gems available",
                    fontWeight = FontWeight.Bold,
                    color = DuoBlue,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Refill Hearts Item
                ShopItemRow(
                    icon = "❤️",
                    title = "Refill Hearts",
                    description = "Restore your hearts to 5 so you never stop learning.",
                    costGems = 100,
                    canAfford = userGems >= 100 && currentHearts < 5,
                    actionLabel = if (currentHearts >= 5) "Full" else "Refill",
                    onBuy = onBuyRefill
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Streak Freeze Item
                ShopItemRow(
                    icon = "🧊",
                    title = "Streak Freeze",
                    description = "Protects your streak if you miss a day of practice ($streakFreezes equipped).",
                    costGems = 200,
                    canAfford = userGems >= 200,
                    actionLabel = "Equip",
                    onBuy = onBuyFreeze
                )
            }
        }
    }
}

@Composable
private fun ShopItemRow(
    icon: String,
    title: String,
    description: String,
    costGems: Int,
    canAfford: Boolean,
    actionLabel: String,
    onBuy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(8.dp))
            DuoTactileButton(
                text = "$costGems 💎",
                onClick = onBuy,
                faceColor = DuoBlue,
                shadowColor = DuoBlueDark,
                enabled = canAfford,
                height = 42.dp,
                bevelDepth = 3.dp
            )
        }
    }
}

// ==========================================
// 6. DUOLINGO LEADERBOARD / LEAGUES DIALOG
// ==========================================
@Composable
fun DuoLeaguesDialog(
    userRank: Int,
    userXp: Int,
    currentLeague: String,
    onDismiss: () -> Unit
) {
    val rivalUsers = listOf(
        Triple(1, "Aarav Sharma", 1420),
        Triple(2, "Kenji Sato", 1280),
        Triple(3, "Sneha Patil", 1190),
        Triple(4, "Monu Gupta (You)", userXp),
        Triple(5, "Priya Nair", 910),
        Triple(6, "Haruto Tanaka", 880),
        Triple(7, "Ananya Verma", 830),
        Triple(8, "Rohan Deshmukh", 760),
        Triple(9, "Vikram Joshi", 710),
        Triple(10, "Yuki Watanabe", 670)
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().testTag("duo_leagues_dialog")
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏆", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = currentLeague, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DuoGold.copy(alpha = 0.2f),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Text(
                        text = "Top 5 promote to Diamond League • 2 days left",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DuoGoldDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(rivalUsers) { _, (rank, name, xp) ->
                        val isUser = rank == 4
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isUser) DuoGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = if (isUser) BorderStroke(1.5.dp, DuoGreen) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = when (rank) {
                                            1 -> "🥇 1"
                                            2 -> "🥈 2"
                                            3 -> "🥉 3"
                                            else -> "   $rank"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = name,
                                        fontWeight = if (isUser) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (isUser) DuoGreenDark else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "$xp XP",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
