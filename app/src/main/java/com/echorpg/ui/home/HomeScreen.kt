package com.echorpg.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class StoryCard(
    val title: String,
    val emoji: String,
    val teaser: String
)

@Composable
fun HomeScreen(
    onStartStory: (String) -> Unit
) {
    val stories = listOf(
        StoryCard(
            title = "Fantasy Hero",
            emoji = "⚔️",
            teaser = "You are the legendary warrior who saved the realm. Now Lira, Elara, Aria and Selene — the most beautiful women in the kingdom — want to reward you."
        ),
        StoryCard(
            title = "Mafia Underworld",
            emoji = "🔫",
            teaser = "You just became the new Don. Sophia, Isabella, Valentina and Bianca — the dangerous and seductive women in the family — want your protection... and much more."
        ),
        StoryCard(
            title = "Demon Realm",
            emoji = "😈",
            teaser = "You fell into the Demon Realm. Lilith, Nyx, Vespera and Morgana — the most powerful and tempting demonesses — have claimed you as theirs."
        ),
        StoryCard(
            title = "Cyberpunk Megacity",
            emoji = "🌃",
            teaser = "You are the most wanted hacker in Neon City. Nova, Kira, Luna and Raven — the lethal cyber beauties — need your skills... and your heart."
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A001F))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Choose Your World",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            color = Color(0xFFFF4D94),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        stories.forEach { story ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { onStartStory(story.title) },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0B38)),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = story.emoji,
                        fontSize = 72.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = story.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = story.teaser,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFCCCCFF),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { onStartStory(story.title) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D94)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("START STORY", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}