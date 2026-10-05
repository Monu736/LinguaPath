package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SupportedLanguage
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseAccent

@Composable
fun LanguageHeader(
    currentLanguage: SupportedLanguage,
    streak: Int,
    xp: Int,
    onLanguageSelected: (SupportedLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Language Selector Pill
        Box {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier
                    .testTag("language_selector_pill")
                    .clickable { dropdownExpanded = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = currentLanguage.flag, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentLanguage.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "▼", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                SupportedLanguage.entries.forEach { lang ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = lang.flag, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${lang.displayName} (${lang.nativeName})",
                                        fontWeight = if (lang == currentLanguage) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = if (lang.isPrimary) "Primary • Comprehensive" else "Secondary • Structured",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        onClick = {
                            dropdownExpanded = false
                            onLanguageSelected(lang)
                        }
                    )
                }
            }
        }

        // Stats Badges (Streak & XP)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Streak Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AmberTertiary.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, AmberTertiary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔥", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$streak d",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AmberTertiary
                    )
                }
            }

            // XP Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = IndigoPrimary.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "⭐", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$xp XP",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun MemoryBadge(state: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (state.uppercase()) {
        "MASTERED" -> Triple(EmeraldSecondary.copy(alpha = 0.15f), EmeraldSecondary, "MASTERED")
        "FAMILIAR" -> Triple(IndigoPrimary.copy(alpha = 0.15f), IndigoPrimary, "FAMILIAR")
        "LEARNING" -> Triple(AmberTertiary.copy(alpha = 0.15f), AmberTertiary, "LEARNING")
        else -> Triple(Color.Gray.copy(alpha = 0.15f), Color.Gray, "NEW")
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun AudioButton(
    onClick: () -> Unit,
    isPlaying: Boolean = false,
    modifier: Modifier = Modifier,
    sizeDp: Int = 40
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(
                if (isPlaying) IndigoPrimary.copy(alpha = 0.25f)
                else MaterialTheme.colorScheme.primaryContainer
            )
            .testTag("audio_speak_button")
    ) {
        Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = "Listen audio pronunciation",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size((sizeDp * 0.55f).dp)
        )
    }
}

@Composable
fun SaveBookmarkButton(
    isSaved: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier.testTag("bookmark_save_button")
    ) {
        Icon(
            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            contentDescription = if (isSaved) "Saved to review" else "Save word",
            tint = if (isSaved) AmberTertiary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
