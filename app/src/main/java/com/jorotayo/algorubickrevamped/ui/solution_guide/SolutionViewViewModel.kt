package com.jorotayo.algorubickrevamped.ui.solution_guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jorotayo.algorubickrevamped.data.Solution
import com.jorotayo.algorubickrevamped.data.SolutionRepository
import com.jorotayo.algorubickrevamped.data.Steps
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SolutionViewUiState(
    val solution: Solution? = null,
    val steps: List<Steps> = emptyList(),
    val showDeleteDialog: Boolean = false,
    val notFound: Boolean = false,
)

class SolutionViewViewModel(
    private val solutionId: Long,
    private val repository: SolutionRepository = SolutionRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(SolutionViewUiState())
    val uiState: StateFlow<SolutionViewUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val solution = repository.getSolution(solutionId)
        if (solution == null) {
            _uiState.update { it.copy(solution = null, steps = emptyList(), notFound = true) }
            return
        }
        _uiState.update {
            it.copy(
                solution = solution,
                steps = repository.stepsFor(solution.solutionName),
                notFound = false,
            )
        }
    }

    fun showDeleteDialog(show: Boolean) {
        _uiState.update { it.copy(showDeleteDialog = show) }
    }

    fun deleteSolution(): Boolean {
        val solution = _uiState.value.solution ?: return false
        repository.removeStepsFor(solution.solutionName)
        repository.removeSolution(solution)
        return true
    }

    companion object {
        fun factory(solutionId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SolutionViewViewModel(solutionId) as T
                }
            }
    }
}
