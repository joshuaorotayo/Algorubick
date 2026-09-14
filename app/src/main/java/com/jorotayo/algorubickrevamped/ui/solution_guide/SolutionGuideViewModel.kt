package com.jorotayo.algorubickrevamped.ui.solution_guide

import androidx.lifecycle.ViewModel
import com.jorotayo.algorubickrevamped.data.Solution
import com.jorotayo.algorubickrevamped.data.SolutionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class SolutionSortMode {
    Name,
    Creator,
}

data class SolutionGuideUiState(
    val solutions: List<Solution> = emptyList(),
    val sortMode: SolutionSortMode = SolutionSortMode.Name,
)

/**
 * Must expose a no-arg constructor for the default viewModel() factory.
 */
class SolutionGuideViewModel : ViewModel() {

    private val repository = SolutionRepository()

    private val _uiState = MutableStateFlow(SolutionGuideUiState())
    val uiState: StateFlow<SolutionGuideUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val sortMode = _uiState.value.sortMode
        _uiState.update {
            it.copy(solutions = loadSorted(sortMode))
        }
    }

    fun setSortMode(mode: SolutionSortMode) {
        _uiState.update {
            it.copy(
                sortMode = mode,
                solutions = loadSorted(mode),
            )
        }
    }

    private fun loadSorted(mode: SolutionSortMode): List<Solution> = when (mode) {
        SolutionSortMode.Name -> repository.getAllSortedByName()
        SolutionSortMode.Creator -> repository.getAllSortedByCreator()
    }
}
