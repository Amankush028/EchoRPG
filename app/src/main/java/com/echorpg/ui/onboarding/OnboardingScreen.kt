package com.echorpg.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    val pages = listOf(
        OnboardingPage(
            title = "Immersive Explicit Stories",
            description = "Long-form (8–12 hour) fantasy & dark roleplay.\nUncensored, graphic, moans and dirty talk included.",
            emoji = "📖"
        ),
        OnboardingPage(
            title = "Create Your Persona",
            description = "Define your name, body, vibe, kinks & limits.\nGirls will remember everything you do.",
            emoji = "🧔"
        ),
        OnboardingPage(
            title = "Unlock Girls Forever",
            description = "Meet girls in stories → they appear instantly.\nOnce unlocked, they are yours in free 1-on-1 or group chat.",
            emoji = "❤️"
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A001F))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPageContent(pages[page])
        }

        // Bottom indicators + button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page dots
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(3) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (isSelected) 10.dp else 8.dp)
                            .background(
                                color = if (isSelected) Color(0xFFFF4D94) else Color.White.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (pagerState.currentPage == 2) {
                        onGetStarted()
                    } else {
                        // Optional: auto-advance
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D94))
            ) {
                Text(
                    text = if (pagerState.currentPage == 2) "GET STARTED" else "NEXT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private data class OnboardingPage(
    val title: String,
    val description: String,
    val emoji: String
)

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Placeholder box — replace with real anime art later (we'll do this in Step 3)
        Box(
            modifier = Modifier
                .size(240.dp)
                .background(Color(0xFF1A0033).copy(alpha = 0.6f), shape = MaterialTheme.shapes.extraLarge),
            contentAlignment = Alignment.Center
        ) {
            Text(text = page.emoji, fontSize = 120.sp)
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFCCCCFF),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp
        )
    }
}