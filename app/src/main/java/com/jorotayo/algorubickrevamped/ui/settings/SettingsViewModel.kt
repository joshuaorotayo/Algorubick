package com.jorotayo.algorubickrevamped.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jorotayo.algorubickrevamped.data.AppSettings
import com.jorotayo.algorubickrevamped.data.SettingsRepository
import com.jorotayo.algorubickrevamped.data.SoundPack
import com.jorotayo.algorubickrevamped.data.ThemeMode
import com.jorotayo.algorubickrevamped.data.VibrationPreset
import com.jorotayo.algorubickrevamped.ui.whatsnew.WhatsNewPrefs
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val repository = SettingsRepository.get(application)
    private val feedbackPlayer = PracticeFeedbackPlayer(application)

    val settings: StateFlow<AppSettings> = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettings(),
    )

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch {
        repository.setThemeMode(mode)
    }

    fun setSoundEnabledCorrect(enabled: Boolean) = viewModelScope.launch {
        repository.setSoundEnabledCorrect(enabled)
    }

    fun setSoundEnabledWrong(enabled: Boolean) = viewModelScope.launch {
        repository.setSoundEnabledWrong(enabled)
    }

    fun setSoundPackCorrect(pack: SoundPack) = viewModelScope.launch {
        repository.setSoundPackCorrect(pack)
    }

    fun setSoundPackWrong(pack: SoundPack) = viewModelScope.launch {
        repository.setSoundPackWrong(pack)
    }

    fun setVibrationEnabled(enabled: Boolean) = viewModelScope.launch {
        repository.setVibrationEnabled(enabled)
    }

    fun setVibrationPresetCorrect(preset: VibrationPreset) = viewModelScope.launch {
        repository.setVibrationPresetCorrect(preset)
    }

    fun setVibrationPresetWrong(preset: VibrationPreset) = viewModelScope.launch {
        repository.setVibrationPresetWrong(preset)
    }

    fun setLearntThresholdPercent(percent: Int) = viewModelScope.launch {
        repository.setLearntThresholdPercent(percent)
    }

    /**
     * When enabled, clears the dismissed version so What's New shows after the next splash.
     * When disabled, marks the current app version as dismissed.
     * A new app version always re-enables showing until the user opts out again.
     */
    fun setShowWhatsNewOnStartup(enabled: Boolean) = viewModelScope.launch {
        if (enabled) {
            repository.setWhatsNewDismissedVersion("")
        } else {
            repository.setWhatsNewDismissedVersion(WhatsNewPrefs.currentVersion)
        }
    }

    fun previewCorrectSound() {
        feedbackPlayer.previewSound(settings.value.soundPackCorrect, correct = true)
    }

    fun previewWrongSound() {
        feedbackPlayer.previewSound(settings.value.soundPackWrong, correct = false)
    }

    fun previewCorrectVibration() {
        feedbackPlayer.previewVibration(settings.value.vibrationPresetCorrect)
    }

    fun previewWrongVibration() {
        feedbackPlayer.previewVibration(settings.value.vibrationPresetWrong)
    }
}
