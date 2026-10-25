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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.TextUnit
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
// 1. TACTILE 3D DUOLINGO BUTTON (PHYSICAL PRESS & SOUND TRANSITIONS)
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
    val view = androidx.compose.ui.platform.LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Tactile physical depression animation
    val animatedOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) bevelDepth else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_press_offset"
    )

    // Bouncy scale transition on press
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.965f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_scale"
    )

    val actualFaceColor = if (enabled) faceColor else Color(0xFFE5E5E5)
    val actualShadowColor = if (enabled) shadowColor else Color(0xFFCECECE)
    val actualTextColor = if (enabled) textColor else Color(0xFFAFAFAF)

    Box(
        modifier = modifier
            .height(height)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = {
                    view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                    onClick()
                }
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
    val view = androidx.compose.ui.platform.LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedOffset by animateDpAsState(
        targetValue = if (isPressed) 3.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "opt_press"
    )

    // Bouncy scale transition when selected or pressed
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.98f
            state == DuoOptionState.SELECTED -> 1.025f
            state == DuoOptionState.CORRECT -> 1.03f
            state == DuoOptionState.WRONG -> 0.99f
            else -> 1.0f
        },
        animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "opt_scale"
    )

    val animatedBorderWidth by animateDpAsState(
        targetValue = if (state != DuoOptionState.DEFAULT) 2.5.dp else 1.5.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "opt_border_w"
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
                onClick = {
                    view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                    onClick()
                }
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
                .border(animatedBorderWidth, borderColor, RoundedCornerShape(16.dp)),
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
// 5. AVATAR WITH FRAME (EQUIPPED COSMETIC BORDER)
// ==========================================
@Composable
fun AvatarWithFrame(
    avatarEmoji: String,
    frameKey: String,
    photoUrl: String? = null,
    size: Dp = 48.dp,
    fontSize: TextUnit = 24.sp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val (borderBrush, borderWidth) = when (frameKey.uppercase()) {
        "BRONZE" -> Brush.sweepGradient(listOf(Color(0xFFCD7F32), Color(0xFF8B4513), Color(0xFFE8974D), Color(0xFFCD7F32))) to 3.dp
        "SILVER" -> Brush.sweepGradient(listOf(Color(0xFFE0E0E0), Color(0xFFA0A0A0), Color(0xFFFFFFFF), Color(0xFFE0E0E0))) to 3.dp
        "GOLD" -> Brush.sweepGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500), Color(0xFFFFF4B8), Color(0xFFFFD700))) to 3.5.dp
        "NEON" -> Brush.sweepGradient(listOf(Color(0xFF00F0FF), Color(0xFFFF007F), Color(0xFF7000FF), Color(0xFF00F0FF))) to 3.5.dp
        "ROYAL" -> Brush.sweepGradient(listOf(Color(0xFF8A2BE2), Color(0xFFFF1493), Color(0xFF4B0082), Color(0xFF8A2BE2))) to 4.dp
        else -> Brush.sweepGradient(listOf(DuoBlue, DuoBlueDark, DuoBlue)) to 2.dp
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(borderWidth, borderBrush, CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (!photoUrl.isNullOrBlank()) {
            coil.compose.AsyncImage(
                model = photoUrl,
                contentDescription = "User Profile Photo",
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        } else {
            Text(text = avatarEmoji, fontSize = fontSize)
        }
    }
}

// ==========================================
// 6. EXPANDED DIAMOND SHOP MODAL (FRAMES, AVATARS, BOOSTS, CONSUMABLES)
// ==========================================
@Composable
fun DuoShopDialog(
    userGems: Int,
    streakFreezes: Int,
    currentHearts: Int,
    equippedFrame: String = "DEFAULT",
    unlockedFrames: String = "DEFAULT",
    equippedAvatar: String = "⚡",
    unlockedAvatars: String = "⚡,🦉",
    activeCategoryName: String = "Food & Dining",
    onBuyRefill: () -> Unit,
    onBuyFreeze: () -> Unit,
    onBuyFrame: (frameKey: String, cost: Int) -> Unit = { _, _ -> },
    onEquipFrame: (frameKey: String) -> Unit = {},
    onBuyAvatar: (avatarEmoji: String, cost: Int) -> Unit = { _, _ -> },
    onEquipAvatar: (avatarEmoji: String) -> Unit = {},
    onUnlockFastPass: (cost: Int) -> Unit = {},
    onDismiss: () -> Unit
) {
    var selectedShopTab by remember { mutableStateOf("FRAMES") }
    val ownedFrames = remember(unlockedFrames) {
        unlockedFrames.split(",").map { it.trim().uppercase() }.toSet()
    }
    val ownedAvatars = remember(unlockedAvatars) {
        unlockedAvatars.split(",").map { it.trim() }.toSet()
    }

    val availableFrames = listOf(
        Triple("BRONZE", "Bronze Polyglot Frame", 150),
        Triple("SILVER", "Silver Linguist Frame", 300),
        Triple("GOLD", "Royal Gold Champion", 500),
        Triple("NEON", "Cyber Neon Glow", 750),
        Triple("ROYAL", "Royal Velvet Legend", 1000)
    )

    val availableAvatars = listOf(
        Triple("🦊", "Clever Fox (Conversationalist)", 250),
        Triple("🦉", "Grandmaster Owl (Grammar Expert)", 400),
        Triple("🦁", "Linguistic Lion (Speech Leader)", 600),
        Triple("🐲", "Mythic Dragon (Master Polyglot)", 1000)
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("duo_shop_dialog")
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💎", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Diamond Bazaar",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Balance & Active Look Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DuoBlue.copy(alpha = 0.1f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AvatarWithFrame(
                                avatarEmoji = equippedAvatar,
                                frameKey = equippedFrame,
                                size = 46.dp,
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Your Look: ${equippedFrame.uppercase()}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Equip frames & avatars below",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DuoBlue,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = "$userGems 💎",
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Category Filter Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val tabs = listOf(
                        "FRAMES" to "🎨 Frames",
                        "AVATARS" to "🎭 Avatars",
                        "BOOSTS" to "⚡ Boosts",
                        "HEARTS" to "❤️ Items"
                    )
                    tabs.forEach { (key, label) ->
                        val isSelected = selectedShopTab == key
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) DuoBlue else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedShopTab = key }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Item List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (selectedShopTab) {
                        "FRAMES" -> {
                            items(availableFrames.size) { idx ->
                                val (fKey, fName, fCost) = availableFrames[idx]
                                val isOwned = ownedFrames.contains(fKey)
                                val isEquipped = equippedFrame.equals(fKey, ignoreCase = true)

                                ShopItemCard(
                                    leadingPreview = {
                                        AvatarWithFrame(
                                            avatarEmoji = equippedAvatar,
                                            frameKey = fKey,
                                            size = 42.dp,
                                            fontSize = 20.sp
                                        )
                                    },
                                    title = fName,
                                    subtitle = if (isEquipped) "Currently Equipped" else if (isOwned) "Owned • Tap to Equip" else "Permanent Profile Frame",
                                    actionLabel = when {
                                        isEquipped -> "EQUIPPED"
                                        isOwned -> "EQUIP"
                                        else -> "$fCost 💎"
                                    },
                                    actionEnabled = when {
                                        isEquipped -> false
                                        isOwned -> true
                                        else -> userGems >= fCost
                                    },
                                    actionColor = when {
                                        isEquipped -> Color.Gray
                                        isOwned -> DuoGreen
                                        else -> DuoBlue
                                    },
                                    onAction = {
                                        if (isOwned) {
                                            onEquipFrame(fKey)
                                        } else {
                                            onBuyFrame(fKey, fCost)
                                        }
                                    }
                                )
                            }
                        }

                        "AVATARS" -> {
                            items(availableAvatars.size) { idx ->
                                val (aEmoji, aName, aCost) = availableAvatars[idx]
                                val isOwned = ownedAvatars.contains(aEmoji)
                                val isEquipped = equippedAvatar == aEmoji

                                ShopItemCard(
                                    leadingPreview = {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = aEmoji, fontSize = 24.sp)
                                        }
                                    },
                                    title = aName,
                                    subtitle = if (isEquipped) "Currently Equipped" else if (isOwned) "Owned • Tap to Equip" else "Unlockable mascot icon",
                                    actionLabel = when {
                                        isEquipped -> "EQUIPPED"
                                        isOwned -> "EQUIP"
                                        else -> "$aCost 💎"
                                    },
                                    actionEnabled = when {
                                        isEquipped -> false
                                        isOwned -> true
                                        else -> userGems >= aCost
                                    },
                                    actionColor = when {
                                        isEquipped -> Color.Gray
                                        isOwned -> DuoGreen
                                        else -> DuoBlue
                                    },
                                    onAction = {
                                        if (isOwned) {
                                            onEquipAvatar(aEmoji)
                                        } else {
                                            onBuyAvatar(aEmoji, aCost)
                                        }
                                    }
                                )
                            }
                        }

                        "BOOSTS" -> {
                            item {
                                ShopItemCard(
                                    leadingPreview = { Text(text = "🔓", fontSize = 28.sp) },
                                    title = "Level Fast-Pass (Next 5 Levels)",
                                    subtitle = "Instantly unlocks the next 5 levels in $activeCategoryName.",
                                    actionLabel = "150 💎",
                                    actionEnabled = userGems >= 150,
                                    actionColor = DuoGold,
                                    onAction = { onUnlockFastPass(150) }
                                )
                            }
                            item {
                                ShopItemCard(
                                    leadingPreview = { Text(text = "🧊", fontSize = 28.sp) },
                                    title = "Streak Freeze",
                                    subtitle = "Protects your streak if you miss a day ($streakFreezes active).",
                                    actionLabel = "200 💎",
                                    actionEnabled = userGems >= 200,
                                    actionColor = DuoBlue,
                                    onAction = onBuyFreeze
                                )
                            }
                        }

                        "HEARTS" -> {
                            item {
                                ShopItemCard(
                                    leadingPreview = { Text(text = "❤️", fontSize = 28.sp) },
                                    title = "Instant Heart Refill",
                                    subtitle = "Restores your hearts to maximum 5 (current: $currentHearts/5).",
                                    actionLabel = if (currentHearts >= 5) "FULL" else "100 💎",
                                    actionEnabled = userGems >= 100 && currentHearts < 5,
                                    actionColor = DuoRed,
                                    onAction = onBuyRefill
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShopItemCard(
    leadingPreview: @Composable () -> Unit,
    title: String,
    subtitle: String,
    actionLabel: String,
    actionEnabled: Boolean,
    actionColor: Color,
    onAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingPreview()
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            DuoTactileButton(
                text = actionLabel,
                onClick = onAction,
                faceColor = actionColor,
                shadowColor = actionColor.copy(alpha = 0.7f),
                enabled = actionEnabled,
                height = 38.dp,
                bevelDepth = 3.dp
            )
        }
    }
}

// ==========================================
// 7. DUOLINGO DAILY QUESTS CARD (WITH ANIMATED REWARD CHEST)
// ==========================================
@Composable
fun DuoDailyQuestsCard(
    earnedXp: Int,
    targetXp: Int = 50,
    completedQuizzes: Int = 2,
    targetQuizzes: Int = 2,
    onClaimReward: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = androidx.compose.ui.platform.LocalView.current
    var isClaimed by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.5.dp, DuoGold.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("duo_daily_quests_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚡", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Daily Quests",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DuoGold.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "RESET IN 14H",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = DuoGoldDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quest 1: XP Progress
            QuestProgressRow(
                icon = "🌟",
                title = "Earn $targetXp XP today",
                current = minOf(earnedXp, targetXp),
                total = targetXp,
                rewardText = "+15 💎",
                barColor = DuoGold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quest 2: Perfect Quizzes
            QuestProgressRow(
                icon = "🎯",
                title = "Complete $targetQuizzes Quiz Levels",
                current = minOf(completedQuizzes, targetQuizzes),
                total = targetQuizzes,
                rewardText = "+20 💎",
                barColor = DuoGreen
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quest 3: AI Partner Practice
            QuestProgressRow(
                icon = "🎙️",
                title = "Speak with AI Partner for 3 mins",
                current = 2,
                total = 3,
                rewardText = "+25 💎",
                barColor = DuoBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Chest Claim Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (isClaimed) "🎁✨" else "🎁", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isClaimed) "Reward Claimed! +50 💎" else "Daily Bonus Chest Ready!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isClaimed) DuoGreenDark else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "2 of 3 quests completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (!isClaimed) {
                    DuoTactileButton(
                        text = "OPEN 🎁",
                        onClick = {
                            view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                            isClaimed = true
                            onClaimReward()
                        },
                        faceColor = DuoGold,
                        shadowColor = DuoGoldDark,
                        height = 38.dp,
                        bevelDepth = 3.dp
                    )
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DuoGreen.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "CLAIMED ✅",
                            fontWeight = FontWeight.ExtraBold,
                            color = DuoGreenDark,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestProgressRow(
    icon: String,
    title: String,
    current: Int,
    total: Int,
    rewardText: String,
    barColor: Color
) {
    val progress = (current.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    val isComplete = current >= total

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = if (isComplete) "COMPLETE! $rewardText" else "$current/$total • $rewardText",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isComplete) DuoGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        androidx.compose.material3.LinearProgressIndicator(
            progress = { progress },
            color = if (isComplete) DuoGreen else barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

// ==========================================
// 8. DUOLINGO CELEBRATION CONFETTI BURST
// ==========================================
@Composable
fun DuoConfettiExplosion(
    modifier: Modifier = Modifier
) {
    val particles = remember {
        val colors = listOf(DuoGreen, DuoGold, DuoBlue, DuoRed, DuoPurple)
        (0..45).map {
            ConfettiParticle(
                xPct = (5..95).random() / 100f,
                speedY = (800..1600).random().toFloat(),
                speedX = (-300..300).random().toFloat(),
                size = (8..18).random().toFloat(),
                color = colors.random()
            )
        }
    }

    val animProgress = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 1800,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            )
        )
    }

    androidx.compose.foundation.Canvas(modifier = modifier.fillMaxSize()) {
        val p = animProgress.value
        particles.forEach { cp ->
            val curY = cp.speedY * p
            val curX = (size.width * cp.xPct) + (cp.speedX * p)
            val alpha = (1f - p).coerceIn(0f, 1f)
            drawCircle(
                color = cp.color.copy(alpha = alpha),
                radius = cp.size * (1f - (p * 0.3f)),
                center = androidx.compose.ui.geometry.Offset(curX, curY)
            )
        }
    }
}

private data class ConfettiParticle(
    val xPct: Float,
    val speedY: Float,
    val speedX: Float,
    val size: Float,
    val color: Color
)
