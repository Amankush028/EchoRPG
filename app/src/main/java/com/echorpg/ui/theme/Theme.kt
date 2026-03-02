package com.echorpg.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF4D94),      // hot pink
    secondary = Color(0xFFBB86FC),     // purple
    background = Color(0xFF0A001F),    // deep erotic black-purple
    surface = Color(0xFF1A0B38),
    onPrimary = Color.White,
    onBackground = Color(0xFFE0D0FF),
    onSurface = Color(0xFFCCCCFF),
)

@Composable
fun EchoRPGTheme(
    darkTheme: Boolean = true,   // forced dark for sexy vibe
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}