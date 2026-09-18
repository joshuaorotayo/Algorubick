package com.jorotayo.algorubickrevamped.ui.whatsnew

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.jorotayo.algorubickrevamped.MainActivity
import com.jorotayo.algorubickrevamped.data.SettingsRepository
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickThemeFromSettings
import kotlinx.coroutines.launch

class WhatsNewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = SettingsRepository.get(this)
        val fromSettings = intent.getBooleanExtra(EXTRA_FROM_SETTINGS, false)

        setContent {
            AlgorubickThemeFromSettings {
                WhatsNewScreen(
                    onContinue = {
                        lifecycleScope.launch {
                            repository.setWhatsNewDismissedVersion(WhatsNewPrefs.currentVersion)
                            finishFlow(fromSettings)
                        }
                    },
                    onDontShowAgain = {
                        lifecycleScope.launch {
                            repository.setWhatsNewDismissedVersion(WhatsNewPrefs.currentVersion)
                            finishFlow(fromSettings)
                        }
                    },
                )
            }
        }
    }

    private fun finishFlow(fromSettings: Boolean) {
        if (!fromSettings) {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }

    companion object {
        const val EXTRA_FROM_SETTINGS = "from_settings"
    }
}
