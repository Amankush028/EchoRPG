package com.echorpg.ui.character.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echorpg.R
import com.echorpg.data.GirlEntity

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProgressiveGirlCard(
    girl: GirlEntity,
    onClick: () -> Unit = {}
) {
    val isUnlocked = girl.isUnlocked

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF160028))
    ) {
        Box {
            AnimatedContent(
                targetState = isUnlocked,
                transitionSpec = {
                    fadeIn() + scaleIn(initialScale = 0.85f) togetherWith
                            fadeOut() + scaleOut(targetScale = 1.15f)
                }
            ) { unlocked ->
                if (unlocked) {
                    Image(
                        painter = painterResource(getUnlockedDrawable(girl.id)),
                        contentDescription = girl.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.girl_silhouette_locked),
                        contentDescription = "Locked",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)))
                }
            }

            // Name / status
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isUnlocked) girl.name else "???",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isUnlocked) Color(0xFFFF4D94) else Color.White.copy(alpha = 0.6f)
                )
                Text(text = girl.fromStory, color = Color.LightGray, fontSize = 14.sp)
            }

            // Sparkle unlock badge
            if (isUnlocked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(Color(0xFFFF4D94), shape = MaterialTheme.shapes.small)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("UNLOCKED", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun getUnlockedDrawable(girlId: String): Int {
    return when {
        girlId.contains("lira") -> R.drawable.lira
        girlId.contains("elara") -> R.drawable.elara
        girlId.contains("aria") -> R.drawable.aria
        girlId.contains("selene") -> R.drawable.selene

        girlId.contains("sophia") -> R.drawable.sophia
        girlId.contains("isabella") -> R.drawable.isabella
        girlId.contains("valentina") -> R.drawable.valentina
        girlId.contains("bianca") -> R.drawable.bianca

        girlId.contains("lilith") -> R.drawable.lilith
        girlId.contains("nyx") -> R.drawable.nyx
        girlId.contains("vespera") -> R.drawable.vespera
        girlId.contains("morgana") -> R.drawable.morgana

        girlId.contains("nova") -> R.drawable.nova
        girlId.contains("kira") -> R.drawable.kira
        girlId.contains("luna") -> R.drawable.luna
        girlId.contains("raven") -> R.drawable.raven

        else -> R.drawable.girl_silhouette_locked
    }
}