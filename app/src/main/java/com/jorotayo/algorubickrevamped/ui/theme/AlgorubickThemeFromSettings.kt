package com.jorotayo.algorubickrevamped.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jorotayo.algorubickrevamped.data.SettingsRepository
import com.jorotayo.algorubickrevamped.data.ThemeMode

@Composable
fun AlgorubickThemeFromSettings(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val settings by SettingsRepository.get(context).settings.collectAsStateWithLifecycle(
        initialValue = com.jorotayo.algorubickrevamped.data.AppSettings(),
    )
    val darkTheme = when (settings.themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }
    AlgorubickTheme(darkTheme = darkTheme, content = content)
}
