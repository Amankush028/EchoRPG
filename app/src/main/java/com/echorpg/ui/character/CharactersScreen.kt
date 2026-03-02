package com.echorpg.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.echorpg.data.AppDatabase
import com.echorpg.data.Girl
import com.echorpg.repository.GirlRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    onGirlSelected: (Girl) -> Unit,
    onGroupChat: (List<Girl>) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = GirlRepository(AppDatabase.getDatabase(context))
    val viewModel: CharactersViewModel = viewModel { CharactersViewModel(repository) }

    val unlockedGirls by viewModel.unlockedGirls.collectAsState(initial = emptyList())

    var selectedGirls by remember { mutableStateOf(setOf<Girl>()) }
    val isMultiSelectMode by remember { derivedStateOf { selectedGirls.isNotEmpty() } }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0A001F))) {
        TopAppBar(
            title = { Text(if (isMultiSelectMode) "${selectedGirls.size} Selected" else "Unlocked Girls", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A0B38)),
            navigationIcon = { IconButton(onClick = onBack) { Text("←", fontSize = 24.sp) } },
            actions = {
                if (isMultiSelectMode && selectedGirls.size >= 2) {
                    IconButton(onClick = { onGroupChat(selectedGirls.toList()) }) {
                        Text("👥", fontSize = 24.sp)
                    }
                }
            }
        )

        if (unlockedGirls.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No girls met yet ❤️", color = Color(0xFF9999CC))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(unlockedGirls) { girl ->
                    val isLocked = girl.name == "???"

                    Card(
                        modifier = Modifier.clickable { onGirlSelected(girl) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLocked) Color(0xFF1A0B38) else Color(0xFF2A1A4A)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                if (isLocked) "👤" else "👩",
                                fontSize = 72.sp,
                                color = if (isLocked) Color(0xFF666688) else Color.White
                            )

                            Text(
                                if (isLocked) "Unknown Girl" else girl.name,
                                fontWeight = FontWeight.Bold,
                                color = if (isLocked) Color(0xFF8888AA) else Color.White,
                                fontSize = 18.sp
                            )

                            Text(girl.fromStory, color = Color(0xFFFF4D94), fontSize = 12.sp)

                            if (isLocked) {
                                Text("Details locked • Talk more...", color = Color(0xFF666688), fontSize = 12.sp)
                            } else {
                                Text("❤️ ${girl.relationshipLevel}/100", color = Color(0xFFFF4D94))
                            }
                        }
                    }
                }
            }
        }
    }
}