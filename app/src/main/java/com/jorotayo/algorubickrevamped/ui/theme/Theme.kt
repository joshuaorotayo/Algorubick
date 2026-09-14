package com.jorotayo.algorubickrevamped.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = White,
    primaryContainer = Color(0xFFD0E8F0),
    onPrimaryContainer = PrimaryDark,
    secondary = Accent,
    onSecondary = White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF115E59),
    tertiary = PrimaryLight,
    onTertiary = White,
    background = SurfaceDim,
    onBackground = Black,
    surface = SurfaceBright,
    onSurface = Black,
    surfaceVariant = SurfaceContainerLow,
    onSurfaceVariant = OnSurfaceMuted,
    surfaceContainerLowest = White,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceBright,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = Color(0xFFD0DEE3),
    outline = OutlineSubtle,
    outlineVariant = Color(0xFFD5E0E4),
    error = ColorRed,
    onError = White,
)

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = PrimaryDark,
    primaryContainer = Primary,
    onPrimaryContainer = White,
    secondary = Accent,
    onSecondary = PrimaryDark,
    secondaryContainer = Color(0xFF0F766E),
    onSecondaryContainer = Color(0xFFCCFBF1),
    tertiary = PrimaryLight,
    onTertiary = White,
    background = Color(0xFF0A1F28),
    onBackground = Color(0xFFE8F0F3),
    surface = Color(0xFF0F2A35),
    onSurface = Color(0xFFE8F0F3),
    surfaceVariant = Color(0xFF1A3A47),
    onSurfaceVariant = Color(0xFFB6C7CE),
    surfaceContainerLowest = Color(0xFF07151B),
    surfaceContainerLow = Color(0xFF0F2A35),
    surfaceContainer = Color(0xFF143340),
    surfaceContainerHigh = Color(0xFF1A3A47),
    surfaceContainerHighest = Color(0xFF244A5A),
    outline = Color(0xFF5B6B73),
    outlineVariant = Color(0xFF2A4552),
    error = ColorRed,
    onError = White,
)

@Composable
fun AlgorubickTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}

@DefaultPreviews
@Composable
private fun AlgorubickThemePreview() {
    AlgorubickTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Algorubick Theme",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Headline Medium",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Title Large / Secondary",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = "Body Large on surface",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Error accent",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
