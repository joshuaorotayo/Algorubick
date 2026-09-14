package com.jorotayo.algorubickrevamped.ui.algorithm

import android.os.SystemClock
import com.jorotayo.algorubickrevamped.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jorotayo.algorubickrevamped.data.Algorithm
import com.jorotayo.algorubickrevamped.data.AlgorithmRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

enum class StudyMode { Learn, Practice }

data class StudyAlgorithmUiState(
    val mode: StudyMode = StudyMode.Learn,
    val current: Algorithm? = null,
    val stepIcons: List<Int> = emptyList(),
    val input: String = "",
    val correctCount: Int = 0,
    val practicedCount: Int = 0,
    val timerText: String = "00:00.00",
    val feedbackTitleRes: Int? = null,
    val feedbackMessageRes: Int? = null,
    val feedbackCorrect: Boolean = false,
    val sessionFinished: Boolean = false,
    val empty: Boolean = false,
)

class StudyAlgorithmViewModel(
    private val mode: StudyMode,
    private val algorithmIds: List<Long>,
    private val algorithmRepository: AlgorithmRepository = AlgorithmRepository(),
) : ViewModel() {

    private val algorithms: List<Algorithm>
    private val session: MutableList<Algorithm> = mutableListOf()
    private var sessionPosition = 0
    private var sessionCorrect = 0
    private var sessionPracticed = 0
    private var timerJob: Job? = null
    private var timerStart = 0L

    private val _uiState = MutableStateFlow(StudyAlgorithmUiState(mode = mode))
    val uiState: StateFlow<StudyAlgorithmUiState> = _uiState.asStateFlow()

    init {
        algorithms = algorithmIds.mapNotNull { algorithmRepository.get(it) }
        if (algorithms.isEmpty()) {
            _uiState.update { it.copy(empty = true) }
        } else if (mode == StudyMode.Learn) {
            bindAlgorithm(algorithms.random(Random))
        } else {
            setupPracticeSession()
        }
    }

    fun updateInput(value: String) = _uiState.update { it.copy(input = value) }

    fun clearFeedback() = _uiState.update {
        it.copy(feedbackTitleRes = null, feedbackMessageRes = null)
    }

    fun checkAnswer() {
        val current = _uiState.value.current ?: return
        val correct = _uiState.value.input.trim() == current.alg
        _uiState.update {
            it.copy(
                feedbackTitleRes = if (correct) R.string.algorithmStudy_feedback_correct_title else R.string.algorithmStudy_feedback_incorrect_title,
                feedbackMessageRes = if (correct) {
                    R.string.algorithmStudy_feedback_correct_message
                } else {
                    R.string.algorithmStudy_feedback_incorrect_message
                },
                feedbackCorrect = correct,
            )
        }

        if (mode == StudyMode.Learn) {
            bindAlgorithm(algorithms.random(Random))
            _uiState.update { it.copy(input = "") }
        } else {
            if (correct) sessionCorrect++
            sessionPracticed++
            current.practiced_number_int = sessionPracticed
            current.practiced_correctly_int = sessionCorrect
            algorithmRepository.put(current)
            advancePractice()
        }
    }

    private fun setupPracticeSession() {
        val length = if (algorithms.size == 1) 5 else 2
        repeat(length) {
            session.addAll(algorithms)
        }
        session.shuffle()
        sessionPosition = 0
        advancePractice()
    }

    private fun advancePractice() {
        if (sessionPosition >= session.size) {
            stopTimer()
            _uiState.update { it.copy(sessionFinished = true) }
            return
        }
        val next = session[sessionPosition++]
        sessionCorrect = next.practiced_correctly_int
        sessionPracticed = next.practiced_number_int
        bindAlgorithm(next)
        _uiState.update { it.copy(input = "") }
        restartTimer()
    }

    private fun bindAlgorithm(algorithm: Algorithm) {
        _uiState.update {
            it.copy(
                current = algorithm,
                stepIcons = AlgMoveImages.stepDrawables(algorithm.alg),
                correctCount = if (mode == StudyMode.Practice) sessionCorrect else algorithm.practiced_correctly_int,
                practicedCount = if (mode == StudyMode.Practice) sessionPracticed else algorithm.practiced_number_int,
            )
        }
    }

    private fun restartTimer() {
        stopTimer()
        timerStart = SystemClock.uptimeMillis()
        _uiState.update { it.copy(timerText = "00:00.00") }
        timerJob = viewModelScope.launch {
            while (isActive) {
                val elapsed = SystemClock.uptimeMillis() - timerStart
                val totalSeconds = (elapsed / 1000).toInt()
                val mins = totalSeconds / 60
                val secs = totalSeconds % 60
                val millis = ((elapsed % 1000) / 10).toInt()
                _uiState.update {
                    it.copy(
                        timerText = String.format(
                            Locale.getDefault(),
                            "%02d:%02d.%02d",
                            mins,
                            secs,
                            millis,
                        ),
                    )
                }
                delay(100)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        stopTimer()
        super.onCleared()
    }

    companion object {
        fun factory(mode: StudyMode, algorithmIds: List<Long>): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return StudyAlgorithmViewModel(mode, algorithmIds) as T
                }
            }
    }
}
