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

        setContent {
            AlgorubickThemeFromSettings {
                WhatsNewScreen(
                    onContinue = {
                        lifecycleScope.launch {
                            repository.setWhatsNewDismissedVersion(WhatsNewContent.CONTENT_VERSION)
                            goToMain()
                        }
                    },
                    onDontShowAgain = {
                        lifecycleScope.launch {
                            repository.setWhatsNewDismissedVersion(WhatsNewContent.CONTENT_VERSION)
                            goToMain()
                        }
                    },
                )
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
