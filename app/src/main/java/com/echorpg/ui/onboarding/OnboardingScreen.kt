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
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val slides = listOf(
        Triple("Immersive\nExplicit Stories", "8–12 hour long-form fantasy & dark roleplay.\nNo limits. No censorship.\nMoans, dirty talk, kinks — exactly how you want it.", "🔥"),
        Triple("Create Your Persona", "Name, body, vibe, kinks, limits.\nYou become the man every girl in the story craves.", "🖤"),
        Triple("Unlock Girls Forever", "Finish any story → girls are permanently unlocked.\nFree 1-on-1 or group chats that remember every filthy detail.", "💋")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A001F))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = slides[page].third,
                        fontSize = 120.sp,
                        modifier = Modifier.padding(bottom = 48.dp)
                    )

                    Text(
                        text = slides[page].first,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFF4D94),
                        textAlign = TextAlign.Center,
                        lineHeight = 42.sp
                    )

                    Spacer(Modifier.height(32.dp))

                    Text(
                        text = slides[page].second,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFCCCCFF),
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )
                }
            }

            // Dots
            Row(
                Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color(0xFFFF4D94) else Color(0xFF555577)
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (pagerState.currentPage == iteration) 12.dp else 8.dp)
                            .background(color, shape = MaterialTheme.shapes.small)
                    )
                }
            }

            // Button
            Button(
                onClick = {
                    if (pagerState.currentPage < 2) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onGetStarted()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D94))
            ) {
                Text(
                    text = if (pagerState.currentPage == 2) "Get Started" else "Continue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}