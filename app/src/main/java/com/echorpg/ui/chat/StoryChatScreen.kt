package com.echorpg.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
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
import com.echorpg.data.ChatMessage
import com.echorpg.data.ChatViewModel
import com.echorpg.data.Persona
import com.echorpg.repository.StoryRepository
import com.echorpg.repository.GirlRepository
import com.echorpg.data.AppDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryChatScreen(
    storyTitle: String,
    persona: Persona,
    chapterToStart: Int = 1,          // ← NEW: supports jumping to any chapter
    onBack: () -> Unit,
    onFinishStory: () -> Unit
) {
    val storyId = storyTitle.lowercase().replace(" ", "_")
    val context = LocalContext.current
    val storyRepo = StoryRepository(AppDatabase.getDatabase(context))
    val girlRepo = GirlRepository(AppDatabase.getDatabase(context))

    val viewModel: ChatViewModel = viewModel {
        ChatViewModel(storyId, persona, storyTitle, storyRepo, girlRepo)
    }

    // Jump to requested chapter if different from saved
    LaunchedEffect(chapterToStart) {
        if (chapterToStart > 1) {
            viewModel.currentChapter = chapterToStart
            viewModel.progress = (chapterToStart - 1) / 10f
        }
    }

    val messages = viewModel.messages
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    // First message only if truly empty
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            val firstNarration = ChatMessage.Narration(
                "Chapter ${viewModel.currentChapter}/10 • The story of $storyTitle begins...",
                chapter = viewModel.currentChapter
            )
            messages.add(firstNarration)
            messages.add(ChatMessage.Ai("I've been waiting for you... the night feels different now that you're here.", "Lira", chapter = viewModel.currentChapter))
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0A001F))) {
        TopAppBar(
            title = { Text("$storyTitle • Chapter ${viewModel.currentChapter}/10", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A0B38)),
            navigationIcon = { IconButton(onClick = onBack) { Text("←", fontSize = 24.sp) } }
        )

        LinearProgressIndicator(
            progress = { viewModel.progress },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = Color(0xFFFF4D94)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
        ) {
            items(messages) { msg ->
                when (msg) {
                    is ChatMessage.User -> {
                        Row(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.End) {
                            Box(modifier = Modifier.background(Color(0xFFFF4D94), RoundedCornerShape(16.dp)).padding(12.dp)) {
                                Text(msg.text, color = Color.Black)
                            }
                        }
                    }
                    is ChatMessage.Ai -> {
                        Row(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.Start) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(msg.name, color = Color(0xFFFF4D94), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(8.dp))
                                    Text("❤️ ${viewModel.relationships[msg.name] ?: 15}/100", fontSize = 12.sp, color = Color(0xFFFF4D94))
                                }
                                Box(modifier = Modifier.background(Color(0xFF2A1A4A), RoundedCornerShape(16.dp)).padding(12.dp)) {
                                    Text(msg.text, color = Color(0xFFCCCCFF))
                                }
                            }
                        }
                    }
                    is ChatMessage.Narration -> {
                        Box(modifier = Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                            Box(modifier = Modifier.background(Color(0xFF333355).copy(0.6f), RoundedCornerShape(12.dp)).padding(12.dp)) {
                                Text(msg.text, color = Color(0xFF9999CC), textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
            if (viewModel.isLoading) {
                item { CircularProgressIndicator(modifier = Modifier.padding(16.dp), color = Color(0xFFFF4D94)) }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF1A0B38)).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("What do you do or say...?") },
                colors = TextFieldDefaults.colors(unfocusedContainerColor = Color(0xFF2A1A4A))
            )

            IconButton(onClick = {
                if (inputText.isNotBlank()) {
                    viewModel.sendMessage(inputText)
                    inputText = ""
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFFFF4D94))
            }
        }
    }
}