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
import java.util.concurrent.atomic.AtomicLong

data class StepDraft(
    val localId: Long,
    val name: String = "",
    val description: String = "",
    val algorithm: String = "",
    val startImage: String = "",
    val endImage: String = "",
)

data class SolutionEditUiState(
    val isEdit: Boolean = false,
    val name: String = "",
    val creator: String = "",
    val description: String = "",
    val iconUri: String = "",
    val steps: List<StepDraft> = emptyList(),
    val nameError: Boolean = false,
    val creatorError: Boolean = false,
    val descriptionError: Boolean = false,
    val showValidationDialog: Boolean = false,
    val showDiscardDialog: Boolean = false,
    val keyboardStepLocalId: Long? = null,
)

class SolutionEditViewModel(
    private val solutionId: Long?,
    private val repository: SolutionRepository = SolutionRepository(),
) : ViewModel() {
    private val localIdSeq = AtomicLong(1)
    private var editingSolutionId: Long = 0
    private var originalSolutionName: String = ""

    private val _uiState = MutableStateFlow(
        SolutionEditUiState(
            isEdit = solutionId != null,
            steps = listOf(newStep()),
        ),
    )
    val uiState: StateFlow<SolutionEditUiState> = _uiState.asStateFlow()

    init {
        if (solutionId != null) {
            loadForEdit(solutionId)
        }
    }

    private fun newStep(): StepDraft = StepDraft(localId = localIdSeq.getAndIncrement())

    private fun loadForEdit(id: Long) {
        val solution = repository.getSolution(id) ?: return
        editingSolutionId = solution.id
        originalSolutionName = solution.solutionName
        val steps = repository.stepsFor(solution.solutionName).map { step ->
            StepDraft(
                localId = localIdSeq.getAndIncrement(),
                name = step.stepName,
                description = step.stepDescription,
                algorithm = step.stepAlgorithm,
                startImage = step.stepImageStart,
                endImage = step.stepImageEnd,
            )
        }
        _uiState.update {
            it.copy(
                isEdit = true,
                name = solution.solutionName,
                creator = solution.solutionCreator,
                description = solution.solutionDescription,
                iconUri = solution.solutionIconLocation,
                steps = steps.ifEmpty { listOf(newStep()) },
            )
        }
    }

    fun updateName(value: String) {
        _uiState.update { it.copy(name = value, nameError = false) }
    }

    fun updateCreator(value: String) {
        _uiState.update { it.copy(creator = value, creatorError = false) }
    }

    fun updateDescription(value: String) {
        _uiState.update { it.copy(description = value, descriptionError = false) }
    }

    fun updateIcon(uri: String) {
        _uiState.update { it.copy(iconUri = uri) }
    }

    fun addStep() {
        _uiState.update { it.copy(steps = it.steps + newStep()) }
    }

    fun updateStepName(localId: Long, value: String) {
        updateStep(localId) { it.copy(name = value) }
    }

    fun updateStepDescription(localId: Long, value: String) {
        updateStep(localId) { it.copy(description = value.take(150)) }
    }

    fun updateStepAlgorithm(localId: Long, value: String) {
        updateStep(localId) { it.copy(algorithm = value) }
    }

    fun updateStepStartImage(localId: Long, uri: String) {
        updateStep(localId) { it.copy(startImage = uri) }
    }

    fun updateStepEndImage(localId: Long, uri: String) {
        updateStep(localId) { it.copy(endImage = uri) }
    }

    private fun updateStep(localId: Long, transform: (StepDraft) -> StepDraft) {
        _uiState.update { state ->
            state.copy(
                steps = state.steps.map { step ->
                    if (step.localId == localId) transform(step) else step
                },
            )
        }
    }

    fun openKeyboard(localId: Long) {
        _uiState.update { it.copy(keyboardStepLocalId = localId) }
    }

    fun closeKeyboard() {
        _uiState.update { it.copy(keyboardStepLocalId = null) }
    }

    fun dismissValidationDialog() {
        _uiState.update { it.copy(showValidationDialog = false) }
    }

    fun showDiscardDialog(show: Boolean) {
        _uiState.update { it.copy(showDiscardDialog = show) }
    }

    fun save(): Boolean {
        val state = _uiState.value
        val nameBlank = state.name.isBlank()
        val creatorBlank = state.creator.isBlank()
        val descriptionBlank = state.description.isBlank()
        if (nameBlank || creatorBlank || descriptionBlank) {
            _uiState.update {
                it.copy(
                    nameError = nameBlank,
                    creatorError = creatorBlank,
                    descriptionError = descriptionBlank,
                    showValidationDialog = true,
                )
            }
            return false
        }

        val solution = Solution(
            solutionName = state.name.trim(),
            solutionCreator = state.creator.trim(),
            solutionDescription = state.description.trim(),
            solutionIconLocation = state.iconUri,
        ).also {
            if (state.isEdit) {
                it.id = editingSolutionId
            }
        }

        if (state.isEdit && originalSolutionName.isNotEmpty()) {
            repository.removeStepsFor(originalSolutionName)
        }

        repository.putSolution(solution)

        val stepsToSave = state.steps.mapIndexedNotNull { index, draft ->
            if (draft.name.isBlank() || draft.description.isBlank()) {
                null
            } else {
                Steps(
                    solutionName = solution.solutionName,
                    stepNumber = index,
                    stepName = draft.name.trim(),
                    stepDescription = draft.description.trim(),
                    stepAlgorithm = draft.algorithm.trim(),
                    stepImageStart = draft.startImage,
                    stepImageEnd = draft.endImage,
                )
            }
        }
        if (stepsToSave.isNotEmpty()) {
            repository.putSteps(stepsToSave)
        }
        return true
    }

    companion object {
        fun factory(solutionId: Long?): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SolutionEditViewModel(solutionId) as T
                }
            }
    }
}
