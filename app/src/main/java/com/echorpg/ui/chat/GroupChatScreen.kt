package com.echorpg.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.echorpg.data.Girl
import com.echorpg.data.Persona
import com.echorpg.data.AppDatabase
import com.echorpg.repository.StoryRepository
import com.echorpg.repository.GirlRepository

@Composable
fun GroupChatScreen(
    girls: List<Girl>,
    persona: Persona,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val storyId = "group_" + girls.joinToString("_") { it.id.toString() }
    val storyTitle = "Group • " + girls.joinToString(", ") { it.name }

    // Reuse the same chat screen (no need to create ChatViewModel here)
    StoryChatScreen(
        storyTitle = storyTitle,
        persona = persona,
        chapterToStart = 1,
        onBack = onBack,
        onFinishStory = { /* Group chat doesn't use ending */ }
    )
}