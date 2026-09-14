package com.jorotayo.algorubickrevamped.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jorotayo.algorubickrevamped.ui.algorithm.AlgorithmEditScreen
import com.jorotayo.algorubickrevamped.ui.algorithm.AlgorithmEditViewModel
import com.jorotayo.algorubickrevamped.ui.algorithm.AlgorithmViewScreen
import com.jorotayo.algorubickrevamped.ui.algorithm.AlgorithmViewViewModel
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickThemeFromSettings

sealed class AlgorithmScreenMode {
    data object Create : AlgorithmScreenMode()
    data class View(val algorithmId: Long) : AlgorithmScreenMode()
    data class Edit(val algorithmId: Long) : AlgorithmScreenMode()
}

class Activity_Algorithm : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val initialMode: AlgorithmScreenMode = when {
            intent.hasExtra("edit") -> AlgorithmScreenMode.Edit(intent.getLongExtra("edit", 0L))
            intent.hasExtra("algorithm_id") ->
                AlgorithmScreenMode.View(intent.getLongExtra("algorithm_id", 0L))
            else -> AlgorithmScreenMode.Create
        }

        setContent {
            AlgorubickThemeFromSettings {
                var mode by remember { mutableStateOf(initialMode) }
                var viewEpoch by remember { mutableIntStateOf(0) }
                var editEpoch by remember { mutableIntStateOf(0) }

                when (val current = mode) {
                    is AlgorithmScreenMode.Create -> {
                        val vm: AlgorithmEditViewModel =
                            viewModel(
                                key = "create",
                                factory = AlgorithmEditViewModel.factory(null),
                            )
                        AlgorithmEditScreen(
                            viewModel = vm,
                            onBackWithoutSave = { finish() },
                            onSaved = { finish() },
                        )
                    }

                    is AlgorithmScreenMode.View -> {
                        val vm: AlgorithmViewViewModel =
                            viewModel(
                                key = "view-${current.algorithmId}-$viewEpoch",
                                factory = AlgorithmViewViewModel.factory(current.algorithmId),
                            )
                        LaunchedEffect(viewEpoch) { vm.refresh() }
                        AlgorithmViewScreen(
                            viewModel = vm,
                            onBack = { finish() },
                            onEdit = {
                                editEpoch++
                                mode = AlgorithmScreenMode.Edit(current.algorithmId)
                            },
                            onLearn = { ids ->
                                startActivity(
                                    Intent(this, Activity_StudyAlgorithm::class.java).putExtra(
                                        "learn",
                                        ids,
                                    ),
                                )
                            },
                            onPractice = { ids ->
                                startActivity(
                                    Intent(this, Activity_StudyAlgorithm::class.java).putExtra(
                                        "practice",
                                        ids,
                                    ),
                                )
                            },
                            onDeleted = { finish() },
                        )
                    }

                    is AlgorithmScreenMode.Edit -> {
                        val vm: AlgorithmEditViewModel =
                            viewModel(
                                key = "edit-${current.algorithmId}-$editEpoch",
                                factory = AlgorithmEditViewModel.factory(current.algorithmId),
                            )
                        AlgorithmEditScreen(
                            viewModel = vm,
                            onBackWithoutSave = {
                                mode = AlgorithmScreenMode.View(current.algorithmId)
                                viewEpoch++
                            },
                            onSaved = {
                                mode = AlgorithmScreenMode.View(current.algorithmId)
                                viewEpoch++
                            },
                        )
                    }
                }
            }
        }
    }
}
