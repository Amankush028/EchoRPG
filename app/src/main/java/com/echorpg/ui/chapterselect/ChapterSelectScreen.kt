package com.echorpg.ui.chapterselect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.echorpg.data.AppDatabase
import com.echorpg.repository.StoryRepository
import com.echorpg.ui.character.CharacterCreationScreen   // for first-time redirect

data class ChapterItem(
    val number: Int,
    val title: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterSelectScreen(
    storyTitle: String,
    onChapterSelected: (Int) -> Unit,   // called when user taps a chapter
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = StoryRepository(AppDatabase.getDatabase(context))
    val viewModel: ChapterSelectViewModel = viewModel { ChapterSelectViewModel(repository, storyTitle) }

    val progress by viewModel.progress.collectAsState(initial = null)

    // First-time check - auto go to Character Creation
    LaunchedEffect(progress) {
        if (progress == null || progress!!.progress == 0f) {
            // First time → go to character creation
            // (we will handle navigation in MainActivity, so we show a nice message for 0.5s then redirect)
            onChapterSelected(0) // special value to trigger character creation
            return@LaunchedEffect
        }
    }

    val currentChapter = progress?.currentChapter ?: 1
    val progressPercent = ((progress?.progress ?: 0f) * 100).toInt()

    // Chapter list (exact names from your screenshot + 5 more to make 10 chapters)
    val chapters = listOf(
        ChapterItem(1, "The Lost City"),
        ChapterItem(2, "Echoes of the Past"),
        ChapterItem(3, "Shadows in the Mist"),
        ChapterItem(4, "Whispers of the Deep"),
        ChapterItem(5, "Beyond the Horizon"),
        ChapterItem(6, "The Forgotten Temple"),
        ChapterItem(7, "Crown of Thorns"),
        ChapterItem(8, "Blood Moon Rising"),
        ChapterItem(9, "The Eternal Flame"),
        ChapterItem(10, "Ascension")
    )

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0A001F))) {
        TopAppBar(
            title = { Text("Chapter", fontWeight = FontWeight.Bold, fontSize = 24.sp) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A0B38)),
            navigationIcon = { IconButton(onClick = onBack) { Text("←", fontSize = 24.sp) } }
        )

        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(chapters) { chapter ->
                val isCompleted = chapter.number < currentChapter
                val isCurrent = chapter.number == currentChapter
                val statusText = when {
                    isCompleted -> "Completed"
                    isCurrent -> "In Progress ($progressPercent%)"
                    else -> "Not Opened"
                }
                val statusColor = when {
                    isCompleted -> Color(0xFF00FF9D)
                    isCurrent -> Color(0xFFFF4D94)
                    else -> Color(0xFF9999CC)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onChapterSelected(chapter.number) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0B38))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = chapter.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = statusText,
                                fontSize = 14.sp,
                                color = Color(0xFF9999CC)
                            )
                        }
                        Text(
                            text = statusText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}