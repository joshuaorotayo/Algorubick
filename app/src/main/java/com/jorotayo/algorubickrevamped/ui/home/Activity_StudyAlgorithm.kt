package com.jorotayo.algorubickrevamped.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jorotayo.algorubickrevamped.ui.algorithm.StudyAlgorithmScreen
import com.jorotayo.algorubickrevamped.ui.algorithm.StudyAlgorithmViewModel
import com.jorotayo.algorubickrevamped.ui.algorithm.StudyMode
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickThemeFromSettings

class Activity_StudyAlgorithm : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val practiceIds = readAlgorithmIds("practice")
        val learnIds = readAlgorithmIds("learn")
        val mode = if (practiceIds.isNotEmpty()) StudyMode.Practice else StudyMode.Learn
        val ids = if (practiceIds.isNotEmpty()) practiceIds else learnIds

        setContent {
            AlgorubickThemeFromSettings {
                val vm: StudyAlgorithmViewModel =
                    viewModel(factory = StudyAlgorithmViewModel.factory(mode, ids))
                StudyAlgorithmScreen(
                    viewModel = vm,
                    onBack = { finish() },
                    onSessionFinished = { finish() },
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readAlgorithmIds(key: String): List<Long> {
        val serializable = intent.getSerializableExtra(key)
        if (serializable is ArrayList<*>) {
            if (serializable.isNotEmpty() && serializable[0] is Long) {
                // New Compose home/view pass real ObjectBox ids.
                return serializable.map { it as Long }
            }
            if (serializable.isNotEmpty() && serializable[0] is Int) {
                // Legacy contract: IntegerArrayList of (id - 1).
                return serializable.map { (it as Int).toLong() + 1L }
            }
        }
        // Legacy Intent path: IntegerArrayList of (id - 1).
        return intent.getIntegerArrayListExtra(key)?.map { it.toLong() + 1L }.orEmpty()
    }
}
