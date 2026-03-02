package com.echorpg.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A001F))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "👑",
            fontSize = 80.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            "Harem King",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFFF4D94)
        )

        Spacer(Modifier.height(32.dp))

        Text(
            "Your Persona",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Sir • 28 • Dominant",
            fontSize = 18.sp,
            color = Color(0xFFBB86FC)
        )

        Spacer(Modifier.height(48.dp))

        Text(
            "4 Girls Unlocked",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(64.dp))

        Button(
            onClick = { /* logout or settings later */ },
            colors = ButtonDefaults.buttonColors(Color(0xFFFF4D94)),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("SETTINGS", fontWeight = FontWeight.Bold)
        }
    }
}