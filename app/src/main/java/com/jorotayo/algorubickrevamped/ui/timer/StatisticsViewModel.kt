package com.jorotayo.algorubickrevamped.ui.timer

import androidx.lifecycle.ViewModel
import com.jorotayo.algorubickrevamped.data.Solve
import com.jorotayo.algorubickrevamped.data.SolveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class StatisticsSortMode {
    SolvedDate,
    CubeSize,
    SolveTime,
}

data class StatisticsUiState(
    val solves: List<Solve> = emptyList(),
    val sortMode: StatisticsSortMode = StatisticsSortMode.SolvedDate,
    val reversed: Boolean = false,
    val pendingDelete: Solve? = null,
)

/**
 * Must expose a no-arg constructor for the default viewModel() factory.
 */
class StatisticsViewModel : ViewModel() {

    private val repository = SolveRepository()

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        applySort(_uiState.value.sortMode, _uiState.value.reversed)
    }

    fun setSortMode(mode: StatisticsSortMode) {
        applySort(mode, reversed = false)
    }

    fun toggleReverseSort() {
        val state = _uiState.value
        applySort(state.sortMode, reversed = !state.reversed)
    }

    fun requestDelete(solve: Solve) {
        _uiState.update { it.copy(pendingDelete = solve) }
    }

    fun dismissDelete() {
        _uiState.update { it.copy(pendingDelete = null) }
    }

    fun confirmDelete() {
        val solve = _uiState.value.pendingDelete ?: return
        repository.remove(solve)
        _uiState.update { it.copy(pendingDelete = null) }
        refresh()
    }

    private fun applySort(mode: StatisticsSortMode, reversed: Boolean) {
        val sorted = when (mode) {
            StatisticsSortMode.SolvedDate -> repository.getAllSortedByDate()
            StatisticsSortMode.CubeSize -> repository.getAllSortedByCubeSize()
            StatisticsSortMode.SolveTime -> repository.getAllSortedByTime()
        }
        _uiState.update {
            it.copy(
                sortMode = mode,
                reversed = reversed,
                solves = if (reversed) sorted.asReversed() else sorted,
            )
        }
    }
}
