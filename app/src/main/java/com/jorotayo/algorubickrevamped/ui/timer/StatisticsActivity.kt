package com.jorotayo.algorubickrevamped.ui.timer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme

class StatisticsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlgorubickTheme {
                StatisticsScreen(onBack = { finish() })
            }
        }
    }
}
