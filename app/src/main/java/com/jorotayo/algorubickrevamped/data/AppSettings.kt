package com.jorotayo.algorubickrevamped.data

enum class ThemeMode {
    System,
    Light,
    Dark,
}

enum class SoundPack {
    Soft,
    Arcade,
    Minimal,
}

enum class VibrationPreset {
    Off,
    Soft,
    Medium,
    Sharp,
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.System,
    val soundEnabledCorrect: Boolean = true,
    val soundEnabledWrong: Boolean = true,
    val soundPackCorrect: SoundPack = SoundPack.Soft,
    val soundPackWrong: SoundPack = SoundPack.Soft,
    val vibrationEnabled: Boolean = true,
    val vibrationPresetCorrect: VibrationPreset = VibrationPreset.Soft,
    val vibrationPresetWrong: VibrationPreset = VibrationPreset.Medium,
    val learntThresholdPercent: Int = 80,
    /** Last What's New content version the user dismissed ("Don't show again"). */
    val whatsNewDismissedVersion: String = "",
)
