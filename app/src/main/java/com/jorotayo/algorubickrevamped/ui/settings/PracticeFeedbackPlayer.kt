package com.jorotayo.algorubickrevamped.ui.settings

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.jorotayo.algorubickrevamped.data.AppSettings
import com.jorotayo.algorubickrevamped.data.SoundPack
import com.jorotayo.algorubickrevamped.data.VibrationPreset

class PracticeFeedbackPlayer(context: Context) {
    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun playCorrect(settings: AppSettings) {
        if (settings.soundEnabledCorrect) {
            playTone(settings.soundPackCorrect, correct = true)
        }
        if (settings.vibrationEnabled) {
            vibrate(settings.vibrationPresetCorrect)
        }
    }

    fun playWrong(settings: AppSettings) {
        if (settings.soundEnabledWrong) {
            playTone(settings.soundPackWrong, correct = false)
        }
        if (settings.vibrationEnabled) {
            vibrate(settings.vibrationPresetWrong)
        }
    }

    fun previewSound(pack: SoundPack, correct: Boolean) {
        playTone(pack, correct)
    }

    fun previewVibration(preset: VibrationPreset) {
        vibrate(preset)
    }

    private fun playTone(pack: SoundPack, correct: Boolean) {
        if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT) return
        val streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (streamVolume <= 0) return

        val (toneType, durationMs) = toneFor(pack, correct)
        runCatching {
            val generator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
            generator.startTone(toneType, durationMs)
            // Release after tone finishes.
            android.os.Handler(appContext.mainLooper).postDelayed({
                runCatching { generator.release() }
            }, (durationMs + 50).toLong())
        }
    }

    private fun toneFor(pack: SoundPack, correct: Boolean): Pair<Int, Int> = when (pack) {
        SoundPack.Soft -> if (correct) {
            ToneGenerator.TONE_PROP_ACK to 180
        } else {
            ToneGenerator.TONE_PROP_NACK to 220
        }
        SoundPack.Arcade -> if (correct) {
            ToneGenerator.TONE_CDMA_PIP to 160
        } else {
            ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD to 280
        }
        SoundPack.Minimal -> if (correct) {
            ToneGenerator.TONE_DTMF_1 to 90
        } else {
            ToneGenerator.TONE_DTMF_0 to 120
        }
    }

    private fun vibrate(preset: VibrationPreset) {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return
        when (preset) {
            VibrationPreset.Off -> return
            VibrationPreset.Soft -> vibratePattern(vib, longArrayOf(0, 30), intArrayOf(0, 40))
            VibrationPreset.Medium -> vibratePattern(vib, longArrayOf(0, 55), intArrayOf(0, 120))
            VibrationPreset.Sharp -> vibratePattern(
                vib,
                longArrayOf(0, 25, 40, 35),
                intArrayOf(0, 200, 0, 180),
            )
        }
    }

    private fun vibratePattern(vibrator: Vibrator, timings: LongArray, amplitudes: IntArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(timings, -1)
        }
    }
}
