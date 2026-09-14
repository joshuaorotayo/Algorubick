package com.jorotayo.algorubickrevamped.ui.notation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class NotationPage {
    Intro,
    Faces,
    Moves,
    Doubles,
    TwoLayer,
    Slices,
    Rotations,
    Algorithms,
}

data class NotationUiState(
    val selectedPage: Int = 0,
)

class NotationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NotationUiState())
    val uiState: StateFlow<NotationUiState> = _uiState.asStateFlow()

    fun selectPage(index: Int) {
        _uiState.value = _uiState.value.copy(selectedPage = index.coerceIn(0, NotationPage.entries.lastIndex))
    }
}
