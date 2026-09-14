package com.jorotayo.algorubickrevamped.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.data.SoundPack
import com.jorotayo.algorubickrevamped.data.VibrationPreset

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticeSettingsScreen(
    viewModel: SettingsViewModel,
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var correctExpanded by rememberSaveable { mutableStateOf(true) }
    var wrongExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_practice_feedback_heading),
            style = MaterialTheme.typography.titleMedium,
        )

        SettingsToggleRow(
            title = stringResource(R.string.settings_practice_sound_correct),
            checked = settings.soundEnabledCorrect,
            onCheckedChange = viewModel::setSoundEnabledCorrect,
        )
        SettingsToggleRow(
            title = stringResource(R.string.settings_practice_sound_wrong),
            checked = settings.soundEnabledWrong,
            onCheckedChange = viewModel::setSoundEnabledWrong,
        )
        SettingsToggleRow(
            title = stringResource(R.string.settings_practice_vibration),
            checked = settings.vibrationEnabled,
            onCheckedChange = viewModel::setVibrationEnabled,
        )

        FeedbackAccordion(
            title = stringResource(R.string.settings_practice_accordion_correct),
            expanded = correctExpanded,
            onToggle = { correctExpanded = !correctExpanded },
        ) {
            Text(
                text = stringResource(R.string.settings_practice_sound_pack_label),
                style = MaterialTheme.typography.labelLarge,
            )
            SoundPackRow(
                selected = settings.soundPackCorrect,
                onSelect = viewModel::setSoundPackCorrect,
            )
            TextButton(onClick = viewModel::previewCorrectSound) {
                Text(stringResource(R.string.settings_practice_preview_sound))
            }
            Text(
                text = stringResource(R.string.settings_practice_vibration_preset_label),
                style = MaterialTheme.typography.labelLarge,
            )
            VibrationPresetRow(
                selected = settings.vibrationPresetCorrect,
                onSelect = viewModel::setVibrationPresetCorrect,
            )
            TextButton(onClick = viewModel::previewCorrectVibration) {
                Text(stringResource(R.string.settings_practice_preview_vibration))
            }
        }

        FeedbackAccordion(
            title = stringResource(R.string.settings_practice_accordion_wrong),
            expanded = wrongExpanded,
            onToggle = { wrongExpanded = !wrongExpanded },
        ) {
            Text(
                text = stringResource(R.string.settings_practice_sound_pack_label),
                style = MaterialTheme.typography.labelLarge,
            )
            SoundPackRow(
                selected = settings.soundPackWrong,
                onSelect = viewModel::setSoundPackWrong,
            )
            TextButton(onClick = viewModel::previewWrongSound) {
                Text(stringResource(R.string.settings_practice_preview_sound))
            }
            Text(
                text = stringResource(R.string.settings_practice_vibration_preset_label),
                style = MaterialTheme.typography.labelLarge,
            )
            VibrationPresetRow(
                selected = settings.vibrationPresetWrong,
                onSelect = viewModel::setVibrationPresetWrong,
            )
            TextButton(onClick = viewModel::previewWrongVibration) {
                Text(stringResource(R.string.settings_practice_preview_vibration))
            }
        }

        Text(
            text = stringResource(R.string.settings_practice_learnt_heading),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(
                R.string.settings_practice_learnt_value,
                settings.learntThresholdPercent,
            ),
            style = MaterialTheme.typography.bodyLarge,
        )
        Slider(
            value = settings.learntThresholdPercent.toFloat(),
            onValueChange = { viewModel.setLearntThresholdPercent(it.toInt()) },
            valueRange = 50f..100f,
            steps = 9,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.settings_practice_learnt_helper),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun FeedbackAccordion(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    content()
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SoundPackRow(
    selected: SoundPack,
    onSelect: (SoundPack) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SoundPack.entries.forEach { pack ->
            FilterChip(
                selected = selected == pack,
                onClick = { onSelect(pack) },
                label = {
                    Text(
                        when (pack) {
                            SoundPack.Soft -> stringResource(R.string.settings_practice_pack_soft)
                            SoundPack.Arcade -> stringResource(R.string.settings_practice_pack_arcade)
                            SoundPack.Minimal -> stringResource(R.string.settings_practice_pack_minimal)
                        },
                    )
                },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VibrationPresetRow(
    selected: VibrationPreset,
    onSelect: (VibrationPreset) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        VibrationPreset.entries.forEach { preset ->
            FilterChip(
                selected = selected == preset,
                onClick = { onSelect(preset) },
                label = {
                    Text(
                        when (preset) {
                            VibrationPreset.Off -> stringResource(R.string.settings_practice_vib_off)
                            VibrationPreset.Soft -> stringResource(R.string.settings_practice_vib_soft)
                            VibrationPreset.Medium -> stringResource(R.string.settings_practice_vib_medium)
                            VibrationPreset.Sharp -> stringResource(R.string.settings_practice_vib_sharp)
                        },
                    )
                },
            )
        }
    }
}
