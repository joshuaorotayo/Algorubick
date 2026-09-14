package com.jorotayo.algorubickrevamped

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jorotayo.algorubickrevamped.data.CategoryRepository
import com.jorotayo.algorubickrevamped.ui.navigation.AlgorubickApp
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )

        ObjectBox.init(this)
        seedCategoriesIfNeeded()

        setContent {
            AlgorubickTheme {
                AlgorubickApp()
            }
        }
    }

    private fun seedCategoriesIfNeeded() {
        val settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE)
        if (!settings.getBoolean("FIRST_RUN", false)) {
            CategoryRepository().seedDefaultsIfEmpty(
                listOf("Default", "Cross", "EOLL", "F2L", "OLL", "PLL", "Triggers"),
            )
            settings.edit().putBoolean("FIRST_RUN", true).apply()
        }
    }
}
