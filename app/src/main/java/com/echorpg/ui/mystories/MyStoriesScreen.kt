package com.echorpg.ui.mystories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.echorpg.data.AppDatabase
import com.echorpg.data.StoryProgressEntity
import com.echorpg.repository.StoryRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyStoriesScreen(
    onStoryResume: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = StoryRepository(AppDatabase.getDatabase(context))
    val viewModel: MyStoriesViewModel = viewModel { MyStoriesViewModel(repository) }

    val progressList by viewModel.progressList.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF6B2F9E))   // exact purple from your image
    ) {
        // Top bar (matches screenshot style)
        TopAppBar(
            title = { Text("Started Stories", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            navigationIcon = { IconButton(onClick = onBack) { Text("←", fontSize = 28.sp) } }
        )

        if (progressList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No stories started yet ❤️\nFinish one to see it here!",
                    color = Color.White,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(progressList) { progress ->
                    StoryCardItem(
                        progress = progress,
                        onResume = { onStoryResume(progress.storyId, progress.personaName) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StoryCardItem(
    progress: StoryProgressEntity,
    onResume: () -> Unit
) {
    val percent = (progress.progress * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0033)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background gradient (matches anime card look in your image)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF4A1A8C).copy(alpha = 0.6f),
                                Color(0xFF1A0033).copy(alpha = 0.9f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = progress.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "$percent%",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBB86FC)   // purple accent from image
                    )
                }

                // Purple progress bar (exact style from image)
                LinearProgressIndicator(
                    progress = { progress.progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = Color(0xFFBB86FC),
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}