package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FashionTechDarkColorScheme = darkColorScheme(
    primary = CreamIvory,
    onPrimary = DeepCharcoalRose,
    primaryContainer = Rosewood,
    onPrimaryContainer = CreamIvory,
    secondary = SkyPowder,
    onSecondary = DeepCharcoalRose,
    secondaryContainer = CardElevatedRose,
    onSecondaryContainer = SkyPowder,
    tertiary = LavenderMauve,
    onTertiary = DeepCharcoalRose,
    background = DeepCharcoalRose,
    onBackground = CreamIvory,
    surface = DarkSurfaceRose,
    onSurface = CreamIvory,
    surfaceVariant = CardElevatedRose,
    onSurfaceVariant = TextMutedRose,
    surfaceContainer = DarkSurfaceRose,
    surfaceContainerHigh = CardElevatedRose,
    outline = BorderLavender,
    outlineVariant = BorderLavender.copy(alpha = 0.6f),
    error = ErrorRose,
    onError = Color.White
)

private val FashionTechLightColorScheme = lightColorScheme(
    primary = Rosewood,
    onPrimary = CreamIvory,
    primaryContainer = CreamIvory,
    onPrimaryContainer = DeepCharcoalRose,
    secondary = LavenderMauve,
    onSecondary = DeepCharcoalRose,
    secondaryContainer = SkyPowderLight,
    onSecondaryContainer = DeepCharcoalRose,
    tertiary = SkyPowder,
    onTertiary = DeepCharcoalRose,
    background = Color(0xFFFBF8F0),
    onBackground = DeepCharcoalRose,
    surface = Color.White,
    onSurface = DeepCharcoalRose,
    surfaceVariant = Color(0xFFF3ECE0),
    onSurfaceVariant = Rosewood,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color(0xFFF8F4EC),
    outline = LavenderMauve,
    outlineVariant = BorderLavender,
    error = ErrorRose,
    onError = Color.White
)

@Composable
fun OutfitPlannerTheme(
    darkTheme: Boolean = true, // Rich dark rosewood & cream aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FashionTechDarkColorScheme else FashionTechLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
