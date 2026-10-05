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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClickableWordAnnotation
import com.example.data.model.GrammarLesson
import com.example.data.model.ReadingPassage
import com.example.data.model.SupportedLanguage
import com.example.trainer.PersonalizationEngine
import com.example.ui.components.AudioButton
import com.example.ui.components.LanguageHeader
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.TrainerUiState

@Composable
fun LearnScreen(
    uiState: TrainerUiState,
    onLanguageSelected: (SupportedLanguage) -> Unit,
    onSpeak: (String, String) -> Unit,
    onCompleteLesson: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLang = uiState.currentLanguage
    val profile = uiState.userProfile
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Curriculum", "Grammar Hub", "Reading Lab", "Script / Alphabet")

    var selectedLessonForDetail by remember { mutableStateOf<GrammarLesson?>(null) }
    var selectedWordAnnotation by remember { mutableStateOf<ClickableWordAnnotation?>(null) }

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
            0 -> CurriculumTab(
                language = currentLang,
                grammarLessons = uiState.grammarLessons,
                onSelectLesson = { selectedLessonForDetail = it }
            )
            1 -> GrammarHubTab(
                lessons = uiState.grammarLessons,
                onSelectLesson = { selectedLessonForDetail = it }
            )
            2 -> ReadingLabTab(
                languageCode = currentLang.code,
                onWordClick = { selectedWordAnnotation = it }
            )
            3 -> AlphabetTab(
                language = currentLang,
                onSpeak = onSpeak
            )
        }
    }

    // Grammar Lesson Detail Dialog
    selectedLessonForDetail?.let { lesson ->
        AlertDialog(
            onDismissRequest = { selectedLessonForDetail = null },
            title = {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Text(
                            text = lesson.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = lesson.explanation,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "💡 Rules & Formulas",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = lesson.rulesJson.replace("[", "").replace("]", "").replace("\"", "").trim(),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "⚠️ Common Traps & Mistakes",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = lesson.commonMistakesJson.replace("[", "").replace("]", "").replace("\"", "").trim(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCompleteLesson(lesson.id, 95)
                        selectedLessonForDetail = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Complete & Earn 25 XP")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedLessonForDetail = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Word Annotation Tooltip Dialog
    selectedWordAnnotation?.let { word ->
        AlertDialog(
            onDismissRequest = { selectedWordAnnotation = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(text = word.word, fontWeight = FontWeight.Bold)
                        if (word.transliteration != null) {
                            Text(
                                text = word.transliteration,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    AudioButton(onClick = { onSpeak(word.word, currentLang.code) })
                }
            },
            text = {
                Column {
                    Text(
                        text = "Pronunciation: /${word.pronunciation}/",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Meaning: ${word.meaning}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "Example:", style = MaterialTheme.typography.labelSmall)
                            Text(text = word.example, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { selectedWordAnnotation = null }) {
                    Text("Got It")
                }
            }
        )
    }
}

@Composable
private fun CurriculumTab(
    language: SupportedLanguage,
    grammarLessons: List<GrammarLesson>,
    onSelectLesson: (GrammarLesson) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "${language.displayName} Structured Learning Path",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Progress sequentially to unlock advanced fluency stages.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            LevelStageHeader("Stage 1: Foundations & Core Mechanics", "Alphabet, sounds, greetings & basic sentence structure")
        }

        items(grammarLessons) { lesson ->
            LessonCard(lesson = lesson, onClick = { onSelectLesson(lesson) })
        }

        item {
            LevelStageHeader("Stage 2: Expressive Conversation", "Complex past/future actions, modal verbs, prepositions")
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Conditionals & Idiomatic Phrasing",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Complete Stage 1 grammar lessons to unlock",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelStageHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LessonCard(lesson: GrammarLesson, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (lesson.isCompleted) EmeraldSecondary.copy(alpha = 0.15f)
                else IndigoPrimary.copy(alpha = 0.15f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (lesson.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = lesson.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (lesson.isCompleted) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldSecondary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Score: ${lesson.score}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GrammarHubTab(
    lessons: List<GrammarLesson>,
    onSelectLesson: (GrammarLesson) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Grammar Hub & Rule Explanations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap on any topic to inspect rules, example sentences, common pitfalls, and mini-drills.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(lessons) { lesson ->
            LessonCard(lesson = lesson, onClick = { onSelectLesson(lesson) })
        }
    }
}

@Composable
private fun ReadingLabTab(
    languageCode: String,
    onWordClick: (ClickableWordAnnotation) -> Unit
) {
    val passages = PersonalizationEngine.getReadingPassages(languageCode)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Interactive Reading Lab",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Read natural stories. Tap on highlighted words to inspect their pronunciation, meaning, and translation.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(passages) { passage ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = passage.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = IndigoPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = passage.level,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = passage.content,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Interactive Word Annotations:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        passage.wordAnnotations.values.forEach { annotation ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = IndigoPrimary.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.4f)),
                                modifier = Modifier.clickable { onWordClick(annotation) }
                            ) {
                                Text(
                                    text = "🔍 ${annotation.word}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Translation:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = passage.translation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlphabetTab(
    language: SupportedLanguage,
    onSpeak: (String, String) -> Unit
) {
    val items = when (language) {
        SupportedLanguage.HINDI -> listOf(
            "अ (a)" to "अनार", "आ (aa)" to "आम", "इ (i)" to "इमली", "ई (ee)" to "ईख",
            "उ (u)" to "उल्लू", "ऊ (oo)" to "ऊन", "ए (e)" to "एक", "ओ (o)" to "ओखली",
            "क (ka)" to "कमल", "ख (kha)" to "खरगोश", "ग (ga)" to "गमला", "घ (gha)" to "घर"
        )
        SupportedLanguage.MARATHI -> listOf(
            "अ (a)" to "अननस", "आ (aa)" to "आई", "इ (i)" to "इमारत", "ई (ee)" to "ईश्वर",
            "क (ka)" to "कमळ", "ख (kha)" to "खडू", "ग (ga)" to "गवत", "घ (gha)" to "घर"
        )
        SupportedLanguage.JAPANESE -> listOf(
            "あ (a)" to "As in 'father'", "い (i)" to "As in 'feet'", "う (u)" to "As in 'food'",
            "え (e)" to "As in 'bed'", "お (o)" to "As in 'boat'",
            "か (ka)" to "Ka", "き (ki)" to "Ki", "く (ku)" to "Ku", "け (ke)" to "Ke", "こ (ko)" to "Ko",
            "さ (sa)" to "Sa", "し (shi)" to "Shi", "す (su)" to "Su", "せ (se)" to "Se", "そ (so)" to "So"
        )
        else -> listOf(
            "A a" to "/eɪ/ - Apple", "B b" to "/biː/ - Book", "C c" to "/siː/ - Cat",
            "D d" to "/diː/ - Dog", "E e" to "/iː/ - Elephant", "F f" to "/ɛf/ - Flower",
            "G g" to "/dʒiː/ - Great", "H h" to "/eɪtʃ/ - Hope", "I i" to "/aɪ/ - Idea"
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "${language.displayName} Script & Phonetics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap on any letter or sound to listen to its clear articulation.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(items) { (symbol, example) ->
            Card(
                onClick = { onSpeak(symbol.substringBefore(" "), language.code) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = symbol,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = example,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Speak",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
