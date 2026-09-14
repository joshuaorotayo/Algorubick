package com.jorotayo.algorubickrevamped.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "algorubick_settings")

class SettingsRepository(context: Context) {
    private val dataStore = context.applicationContext.settingsDataStore

    val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[Keys.THEME_MODE].toEnum(ThemeMode.System),
            soundEnabledCorrect = prefs[Keys.SOUND_ENABLED_CORRECT] ?: true,
            soundEnabledWrong = prefs[Keys.SOUND_ENABLED_WRONG] ?: true,
            soundPackCorrect = prefs[Keys.SOUND_PACK_CORRECT].toEnum(SoundPack.Soft),
            soundPackWrong = prefs[Keys.SOUND_PACK_WRONG].toEnum(SoundPack.Soft),
            vibrationEnabled = prefs[Keys.VIBRATION_ENABLED] ?: true,
            vibrationPresetCorrect = prefs[Keys.VIBRATION_PRESET_CORRECT].toEnum(VibrationPreset.Soft),
            vibrationPresetWrong = prefs[Keys.VIBRATION_PRESET_WRONG].toEnum(VibrationPreset.Medium),
            learntThresholdPercent = (prefs[Keys.LEARNT_THRESHOLD] ?: 80).coerceIn(50, 100),
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setSoundEnabledCorrect(enabled: Boolean) {
        dataStore.edit { it[Keys.SOUND_ENABLED_CORRECT] = enabled }
    }

    suspend fun setSoundEnabledWrong(enabled: Boolean) {
        dataStore.edit { it[Keys.SOUND_ENABLED_WRONG] = enabled }
    }

    suspend fun setSoundPackCorrect(pack: SoundPack) {
        dataStore.edit { it[Keys.SOUND_PACK_CORRECT] = pack.name }
    }

    suspend fun setSoundPackWrong(pack: SoundPack) {
        dataStore.edit { it[Keys.SOUND_PACK_WRONG] = pack.name }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.VIBRATION_ENABLED] = enabled }
    }

    suspend fun setVibrationPresetCorrect(preset: VibrationPreset) {
        dataStore.edit { it[Keys.VIBRATION_PRESET_CORRECT] = preset.name }
    }

    suspend fun setVibrationPresetWrong(preset: VibrationPreset) {
        dataStore.edit { it[Keys.VIBRATION_PRESET_WRONG] = preset.name }
    }

    suspend fun setLearntThresholdPercent(percent: Int) {
        dataStore.edit { it[Keys.LEARNT_THRESHOLD] = percent.coerceIn(50, 100) }
    }

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SOUND_ENABLED_CORRECT = booleanPreferencesKey("sound_enabled_correct")
        val SOUND_ENABLED_WRONG = booleanPreferencesKey("sound_enabled_wrong")
        val SOUND_PACK_CORRECT = stringPreferencesKey("sound_pack_correct")
        val SOUND_PACK_WRONG = stringPreferencesKey("sound_pack_wrong")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val VIBRATION_PRESET_CORRECT = stringPreferencesKey("vibration_preset_correct")
        val VIBRATION_PRESET_WRONG = stringPreferencesKey("vibration_preset_wrong")
        val LEARNT_THRESHOLD = intPreferencesKey("learnt_threshold_percent")
    }

    private inline fun <reified T : Enum<T>> String?.toEnum(default: T): T =
        this?.let { runCatching { enumValueOf<T>(it) }.getOrNull() } ?: default

    companion object {
        @Volatile
        private var instance: SettingsRepository? = null

        fun get(context: Context): SettingsRepository =
            instance ?: synchronized(this) {
                instance ?: SettingsRepository(context).also { instance = it }
            }
    }
}
