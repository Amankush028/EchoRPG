package com.echorpg.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.echorpg.data.Girl
import com.echorpg.data.GirlEntity
import com.echorpg.repository.GirlRepository
import com.echorpg.ui.character.components.ProgressiveGirlCard
import androidx.compose.runtime.collectAsState   // ← THIS IS THE CORRECT IMPORT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    repository: GirlRepository,
    onGirlSelected: (Girl) -> Unit,
    onGroupChat: (List<Girl>) -> Unit,
    onBack: () -> Unit = {}
) {
    val girls by repository.getAllUnlockedGirlsEntity()
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Girls") },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { padding ->
        if (girls.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No girls unlocked yet.\nStart a story to meet them!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(girls) { girlEntity ->
                    ProgressiveGirlCard(
                        girl = girlEntity,
                        onClick = {
                            val simpleGirl = Girl(
                                id = girlEntity.id.toIntOrNull() ?: 0,
                                name = girlEntity.name,
                                fromStory = girlEntity.fromStory,
                                status = girlEntity.status,
                                relationshipLevel = girlEntity.relationshipLevel
                            )
                            onGirlSelected(simpleGirl)
                        }
                    )
                }
            }
        }
    }
}