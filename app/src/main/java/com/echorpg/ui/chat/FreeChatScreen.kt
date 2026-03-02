package com.echorpg.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.echorpg.data.Persona
import com.echorpg.data.Girl
import com.echorpg.data.AppDatabase
import com.echorpg.repository.StoryRepository

@Composable
fun FreeChatScreen(
    girl: Girl,
    persona: Persona
) {
    val context = LocalContext.current
    val storyRepo = StoryRepository(AppDatabase.getDatabase(context))
    val storyId = "free_${girl.id}"   // special ID for free chat

    StoryChatScreen(   // reuse the same beautiful chat UI
        storyTitle = "Free Chat • ${girl.name}",
        persona = persona,
        onBack = { /* handled by nav */ },
        onFinishStory = { /* not used in free chat */ }
    )
}