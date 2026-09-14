package com.jorotayo.algorubickrevamped.ui.algorithm

import androidx.lifecycle.ViewModel
import com.jorotayo.algorubickrevamped.data.Algorithm
import com.jorotayo.algorubickrevamped.data.AlgorithmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AlgorithmViewUiState(
    val algorithm: Algorithm? = null,
    val showDeleteDialog: Boolean = false,
    val notFound: Boolean = false,
    val deleted: Boolean = false,
)

class AlgorithmViewViewModel(
    private val algorithmId: Long,
    private val algorithmRepository: AlgorithmRepository = AlgorithmRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlgorithmViewUiState())
    val uiState: StateFlow<AlgorithmViewUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val alg = algorithmRepository.get(algorithmId)
        _uiState.update {
            it.copy(
                algorithm = alg,
                notFound = alg == null,
            )
        }
    }

    fun requestDelete() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun dismissDelete() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun confirmDelete() {
        val alg = _uiState.value.algorithm ?: return
        algorithmRepository.remove(alg)
        _uiState.update { it.copy(showDeleteDialog = false, deleted = true) }
    }

    /** Study activity expects algorithm ids (not list indices). */
    fun studyIdList(): ArrayList<Long> {
        val id = _uiState.value.algorithm?.id ?: algorithmId
        return arrayListOf(id)
    }

    companion object {
        fun factory(algorithmId: Long): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return AlgorithmViewViewModel(algorithmId) as T
                }
            }
    }
}
