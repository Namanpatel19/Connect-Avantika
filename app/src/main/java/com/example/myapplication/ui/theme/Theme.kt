package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    onPrimary = Color.White,
    secondary = SecondaryGreen,
    onSecondary = Color.White,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onBackground = TextDark,
    onSurface = TextDark,
    outline = BorderColor
)

@Composable
fun AvantikaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Sticking to Light Theme as per screenshots
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
