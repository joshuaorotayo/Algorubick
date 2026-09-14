package com.jorotayo.algorubickrevamped.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.data.Algorithm
import com.jorotayo.algorubickrevamped.data.AlgorithmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AlgorithmSortMode(val labelRes: Int) {
    CreatedDate(R.string.home_sort_created_date),
    AlgorithmName(R.string.home_sort_algorithm_name),
    Category(R.string.home_sort_category),
}

data class AlgorithmHomeUiState(
    val algorithms: List<Algorithm> = emptyList(),
    val sortMode: AlgorithmSortMode = AlgorithmSortMode.CreatedDate,
    val searchQuery: String = "",
    val selectionMode: Boolean = false,
    val selectedIds: Set<Long> = emptySet(),
    val totalCount: Int = 0,
)

class AlgorithmHomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlgorithmRepository()

    private val _uiState = MutableStateFlow(AlgorithmHomeUiState())
    val uiState: StateFlow<AlgorithmHomeUiState> = _uiState.asStateFlow()

    fun load() {
        repository.ensureDefaultsLoaded(getApplication())
        applyList(repository.getAll())
    }

    fun setSort(mode: AlgorithmSortMode) {
        _uiState.update { it.copy(sortMode = mode) }
        applyList(repository.getAll())
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyList(repository.getAll())
    }

    fun toggleFavourite(id: Long) {
        val alg = repository.get(id) ?: return
        alg.toggleFavourite()
        repository.put(alg)
        if (_uiState.value.selectionMode) {
            clearSelection()
        }
        applyList(repository.getAll())
    }

    fun toggleLearnt(id: Long) {
        val alg = repository.get(id) ?: return
        alg.toggleLearnt()
        repository.put(alg)
        applyList(repository.getAll())
    }

    fun toggleSelection(id: Long) {
        _uiState.update { state ->
            val next = state.selectedIds.toMutableSet()
            if (!next.add(id)) next.remove(id)
            state.copy(
                selectedIds = next,
                selectionMode = next.isNotEmpty(),
            )
        }
    }

    fun enterSelection(id: Long) {
        _uiState.update {
            it.copy(selectionMode = true, selectedIds = it.selectedIds + id)
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectionMode = false, selectedIds = emptySet()) }
    }

    fun getSelectedIds(): List<Long> = _uiState.value.selectedIds.toList()

    private fun applyList(source: List<Algorithm>) {
        val query = _uiState.value.searchQuery.trim()
        val filtered = if (query.isEmpty()) {
            source
        } else {
            source.filter { alg ->
                alg.alg_name.contains(query, ignoreCase = true) ||
                    alg.alg.contains(query, ignoreCase = true) ||
                    alg.category.contains(query, ignoreCase = true) ||
                    alg.alg_description.contains(query, ignoreCase = true)
            }
        }
        val sorted = sortAlgorithms(filtered, _uiState.value.sortMode)
        _uiState.update { state ->
            val stillSelected = state.selectedIds.intersect(sorted.map { it.id }.toSet())
            state.copy(
                algorithms = sorted,
                totalCount = sorted.size,
                selectedIds = stillSelected,
                selectionMode = state.selectionMode && stillSelected.isNotEmpty(),
            )
        }
    }

    private fun sortAlgorithms(
        algorithms: List<Algorithm>,
        mode: AlgorithmSortMode,
    ): List<Algorithm> {
        val list = algorithms.toMutableList()
        when (mode) {
            AlgorithmSortMode.AlgorithmName -> list.sortWith(Algorithm.CompareAlgorithmName())
            AlgorithmSortMode.Category -> list.sortWith(Algorithm.CompareCategory())
            AlgorithmSortMode.CreatedDate -> list.sortWith(Algorithm.CompareCreatedDate())
        }
        return list
    }
}
