package com.example.echonote.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color(0xFF003049),
    primaryVariant = Color(0xFF002239),
    secondary = Color(0xFF0077A3)
)

private val LightColorPalette = lightColors(
    primary = Color(0xFF003049),
    primaryVariant = Color(0xFF00496E),
    secondary = Color(0xFF79D2E6)
)

@Composable
fun EchoNoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}
