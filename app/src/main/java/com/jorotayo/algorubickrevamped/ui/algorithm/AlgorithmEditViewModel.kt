package com.jorotayo.algorubickrevamped.ui.algorithm

import androidx.lifecycle.ViewModel
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.data.Algorithm
import com.jorotayo.algorubickrevamped.data.AlgorithmRepository
import com.jorotayo.algorubickrevamped.data.Category
import com.jorotayo.algorubickrevamped.data.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AlgorithmEditUiState(
    val isEdit: Boolean = false,
    val name: String = "",
    val algorithm: String = "",
    val description: String = "",
    val category: String = "Default",
    val categories: List<Category> = emptyList(),
    val favourite: Boolean = false,
    val custom: Boolean = false,
    val imageUri: String? = null,
    val nameErrorRes: Int? = null,
    val algorithmErrorRes: Int? = null,
    val descriptionErrorRes: Int? = null,
    val showKeyboard: Boolean = false,
    val showUnsavedDialog: Boolean = false,
    val saved: Boolean = false,
    val exitWithoutSave: Boolean = false,
)

class AlgorithmEditViewModel(
    private val editId: Long? = null,
    private val algorithmRepository: AlgorithmRepository = AlgorithmRepository(),
    private val categoryRepository: CategoryRepository = CategoryRepository(),
) : ViewModel() {

    private var existing: Algorithm? = null
    private var baselineSignature: String = ""

    private val _uiState = MutableStateFlow(AlgorithmEditUiState(isEdit = editId != null))
    val uiState: StateFlow<AlgorithmEditUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        val categories = categoryRepository.getAll()
        val defaultCategory = categories.firstOrNull()?.category_name ?: "Default"
        if (editId != null) {
            existing = algorithmRepository.get(editId)
            val alg = existing
            if (alg != null) {
                _uiState.update {
                    it.copy(
                        isEdit = true,
                        name = alg.alg_name.orEmpty(),
                        algorithm = alg.alg.orEmpty(),
                        description = alg.alg_description.orEmpty(),
                        category = alg.category ?: defaultCategory,
                        categories = categories,
                        favourite = alg.favourite_alg,
                        custom = alg.custom_alg,
                        imageUri = alg.algorithm_icon,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(categories = categories, category = defaultCategory)
                }
            }
        } else {
            _uiState.update {
                it.copy(categories = categories, category = defaultCategory)
            }
        }
        baselineSignature = signatureOf(_uiState.value)
    }

    fun updateName(value: String) = _uiState.update { it.copy(name = value, nameErrorRes = null) }
    fun updateAlgorithm(value: String) =
        _uiState.update { it.copy(algorithm = value, algorithmErrorRes = null) }

    fun updateDescription(value: String) =
        _uiState.update { it.copy(description = value, descriptionErrorRes = null) }

    fun updateCategory(value: String) = _uiState.update { it.copy(category = value) }
    fun updateFavourite(value: Boolean) = _uiState.update { it.copy(favourite = value) }
    fun updateCustom(value: Boolean) = _uiState.update { it.copy(custom = value) }
    fun updateImageUri(uri: String?) = _uiState.update { it.copy(imageUri = uri) }

    fun showKeyboard(show: Boolean) = _uiState.update { it.copy(showKeyboard = show) }

    fun requestExit() {
        if (isDirty()) {
            _uiState.update { it.copy(showUnsavedDialog = true) }
        } else {
            _uiState.update { it.copy(exitWithoutSave = true) }
        }
    }

    fun dismissUnsaved() = _uiState.update { it.copy(showUnsavedDialog = false) }

    fun confirmDiscard() {
        _uiState.update { it.copy(showUnsavedDialog = false, exitWithoutSave = true) }
    }

    fun consumeNavigation() {
        _uiState.update { it.copy(saved = false, exitWithoutSave = false) }
    }

    fun isDirty(): Boolean = signatureOf(_uiState.value) != baselineSignature

    fun save(): Boolean {
        val state = _uiState.value
        var valid = true
        var nameErrorRes: Int? = null
        var algorithmErrorRes: Int? = null
        var descriptionErrorRes: Int? = null
        if (state.name.isBlank()) {
            nameErrorRes = R.string.algorithmEdit_field_name_error
            valid = false
        }
        if (state.algorithm.isBlank()) {
            algorithmErrorRes = R.string.algorithmEdit_field_algorithm_error
            valid = false
        }
        if (state.description.isBlank()) {
            descriptionErrorRes = R.string.algorithmEdit_field_description_error
            valid = false
        }
        if (!valid) {
            _uiState.update {
                it.copy(
                    nameErrorRes = nameErrorRes,
                    algorithmErrorRes = algorithmErrorRes,
                    descriptionErrorRes = descriptionErrorRes,
                )
            }
            return false
        }

        val alg = existing ?: Algorithm()
        alg.alg_name = state.name.trim()
        alg.alg = state.algorithm.trim()
        alg.alg_description = state.description.trim()
        alg.category = state.category
        alg.favourite_alg = state.favourite
        alg.custom_alg = state.custom
        if (!state.imageUri.isNullOrBlank()) {
            alg.algorithm_icon = state.imageUri
        }
        if (existing == null) {
            alg.practiced_correctly_int = 0
            alg.practiced_number_int = 0
        }
        alg.setCreatedTime()
        algorithmRepository.put(alg)
        baselineSignature = signatureOf(state)
        _uiState.update { it.copy(saved = true) }
        return true
    }

    private fun signatureOf(state: AlgorithmEditUiState): String {
        return listOf(
            state.name,
            state.algorithm,
            state.description,
            state.category,
            state.favourite.toString(),
            state.custom.toString(),
            state.imageUri.orEmpty(),
        ).joinToString("|")
    }

    companion object {
        fun factory(editId: Long?): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return AlgorithmEditViewModel(editId) as T
                }
            }
    }
}
