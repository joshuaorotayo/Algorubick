package com.jorotayo.algorubickrevamped.ui.solution_guide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme

sealed class SolutionScreenMode {
    data object Create : SolutionScreenMode()
    data class View(val solutionId: Long) : SolutionScreenMode()
    data class Edit(val solutionId: Long) : SolutionScreenMode()
}

class SolutionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val initialMode: SolutionScreenMode =
            if (intent.hasExtra(EXTRA_SOLUTION)) {
                SolutionScreenMode.View(intent.getLongExtra(EXTRA_SOLUTION, 0L))
            } else {
                SolutionScreenMode.Create
            }

        setContent {
            AlgorubickTheme {
                var mode by remember { mutableStateOf(initialMode) }

                when (val current = mode) {
                    is SolutionScreenMode.Create -> SolutionEditScreen(
                        solutionId = null,
                        onSaved = { finish() },
                        onNavigateBack = { finish() },
                    )

                    is SolutionScreenMode.View -> SolutionViewScreen(
                        solutionId = current.solutionId,
                        onEdit = { mode = SolutionScreenMode.Edit(current.solutionId) },
                        onDeleted = { finish() },
                        onNavigateBack = { finish() },
                    )

                    is SolutionScreenMode.Edit -> SolutionEditScreen(
                        solutionId = current.solutionId,
                        onSaved = { mode = SolutionScreenMode.View(current.solutionId) },
                        onNavigateBack = { mode = SolutionScreenMode.View(current.solutionId) },
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_SOLUTION = "Solution"
    }
}
