package com.echorpg.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echorpg.data.Persona

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCreationScreen(
    storyTitle: String,
    onBeginStory: (Persona) -> Unit
) {
    var name by remember { mutableStateOf("You") }
    var title by remember { mutableStateOf("Sir") }
    var age by remember { mutableStateOf("28") }
    var appearance by remember { mutableStateOf("Tall, muscular, dark hair, intense eyes, commanding presence") }
    var selectedVibe by remember { mutableStateOf("Dominant") }
    var selectedKinks by remember { mutableStateOf(listOf("Rough", "Dirty Talk", "Breeding", "Dominance")) }
    var hardLimits by remember { mutableStateOf("No blood, no underage, no scat") }

    val vibes = listOf("Dominant", "Sweet", "Shy", "Sadistic", "Playful", "Caring Daddy", "Brat Tamer")
    val allKinks = listOf("Rough", "Gentle", "Dirty Talk", "Breeding", "Bondage", "Public", "Anal", "Oral", "Praise", "Degradation")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A001F))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Who are you in this world?",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            color = Color(0xFFFF4D94)
        )
        Text(
            text = "in $storyTitle",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFFCCCCFF)
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Your Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("How girls call you (Daddy / Sir / Master...)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Your Age") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = appearance,
            onValueChange = { appearance = it },
            label = { Text("Body & Appearance (girls will describe you like this)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(Modifier.height(32.dp))

        // Vibe chips (2 per row - stable)
        Text("Your Personality Vibe", style = MaterialTheme.typography.titleMedium, color = Color(0xFFFF4D94))
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            vibes.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { vibe ->
                        FilterChip(
                            selected = selectedVibe == vibe,
                            onClick = { selectedVibe = vibe },
                            label = { Text(vibe) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        // Kinks chips (2 per row - stable)
        Text("Main Kinks (tap multiple)", style = MaterialTheme.typography.titleMedium, color = Color(0xFFFF4D94))
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            allKinks.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { kink ->
                        val isSelected = selectedKinks.contains(kink)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedKinks = if (isSelected) selectedKinks - kink else selectedKinks + kink
                            },
                            label = { Text(kink) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = hardLimits,
            onValueChange = { hardLimits = it },
            label = { Text("Hard Limits (AI will never cross these)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = {
                val persona = Persona(
                    name = name,
                    title = title,
                    age = age,
                    appearance = appearance,
                    vibe = selectedVibe,
                    kinks = selectedKinks,
                    hardLimits = hardLimits
                )
                onBeginStory(persona)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D94)),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("BEGIN THE STORY", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}