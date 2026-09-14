package com.jorotayo.algorubickrevamped.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.data.ThemeMode
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme
import com.jorotayo.algorubickrevamped.ui.theme.DefaultPreviews

@Composable
fun DisplaySettingsScreen(
    viewModel: SettingsViewModel,
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_display_appearance_heading),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.settings_display_appearance_helper),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = settings.themeMode == mode,
                    onClick = { viewModel.setThemeMode(mode) },
                    label = {
                        Text(
                            when (mode) {
                                ThemeMode.System -> stringResource(R.string.settings_display_theme_system)
                                ThemeMode.Light -> stringResource(R.string.settings_display_theme_light)
                                ThemeMode.Dark -> stringResource(R.string.settings_display_theme_dark)
                            },
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Text(
            text = stringResource(R.string.settings_display_preview_heading),
            style = MaterialTheme.typography.titleMedium,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HomeThemePreviewCard(
                darkTheme = false,
                selected = settings.themeMode == ThemeMode.Light,
                label = stringResource(R.string.settings_display_theme_light),
                onClick = { viewModel.setThemeMode(ThemeMode.Light) },
                modifier = Modifier.weight(1f),
            )
            HomeThemePreviewCard(
                darkTheme = true,
                selected = settings.themeMode == ThemeMode.Dark,
                label = stringResource(R.string.settings_display_theme_dark),
                onClick = { viewModel.setThemeMode(ThemeMode.Dark) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@DefaultPreviews
@Composable
private fun DisplaySettingsScreenPreview() {
    AlgorubickTheme {
        Text(stringResource(R.string.settings_display_appearance_heading))
    }
}
