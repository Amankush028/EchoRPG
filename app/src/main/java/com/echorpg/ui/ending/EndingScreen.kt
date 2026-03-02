package com.echorpg.ui.ending

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echorpg.data.Girl
import com.echorpg.data.Persona

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EndingScreen(
    storyTitle: String,
    persona: Persona,
    unlockedGirls: List<Girl>,
    onFreeChat: () -> Unit,
    onReplay: () -> Unit,
    onBackToHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A001F)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dramatic Title
            Text(
                text = "STORY COMPLETE",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFF4D94)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = storyTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFCCCCFF)
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "You completed the journey, ${persona.title}.\n\nYour choices shaped everything.",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(48.dp))

            Text(
                text = "GIRLS UNLOCKED FOREVER",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFF4D94)
            )

            Spacer(Modifier.height(16.dp))

            // Girls Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(unlockedGirls) { girl ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0B38))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("👩", fontSize = 52.sp)
                            Text(girl.name, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(girl.status, fontSize = 12.sp, color = Color(0xFFFF4D94))
                        }
                    }
                }
            }

            Spacer(Modifier.height(48.dp))

            // Main Buttons
            Button(
                onClick = onFreeChat,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D94)),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("FREE CHAT WITH UNLOCKED GIRLS", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onReplay,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("REPLAY THIS STORY")
            }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = onBackToHome) {
                Text("BACK TO HOME", color = Color(0xFF9999CC))
            }
        }
    }
}