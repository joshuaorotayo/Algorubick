package com.jorotayo.algorubickrevamped.ui.timer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickThemeFromSettings

class StatisticsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlgorubickThemeFromSettings {
                StatisticsScreen(onBack = { finish() })
            }
        }
    }
}
