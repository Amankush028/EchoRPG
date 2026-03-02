package com.echorpg.ui.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echorpg.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500)
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2200)          // dramatic pause
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A001F)),   // our deep erotic purple-black
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo - using default launcher icon for now (we'll replace with custom later)
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "EchoRPG Logo",
                modifier = Modifier
                    .size(180.dp)
                    .alpha(alpha)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "ECHO RPG",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 8.sp,
                    color = Color(0xFFFF4D94)   // hot pink
                ),
                modifier = Modifier.alpha(alpha)
            )

            Text(
                text = "Unleash Your Desires",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFCCCCFF),
                modifier = Modifier.alpha(alpha)
            )

            Spacer(modifier = Modifier.height(80.dp))

            CircularProgressIndicator(
                color = Color(0xFFFF4D94),
                strokeWidth = 5.dp,
                modifier = Modifier.size(52.dp)
            )
        }
    }
}