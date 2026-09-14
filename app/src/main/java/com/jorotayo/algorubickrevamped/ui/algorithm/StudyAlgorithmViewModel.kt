package com.jorotayo.algorubickrevamped.ui.algorithm

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.data.Algorithm
import com.jorotayo.algorubickrevamped.data.AlgorithmRepository
import com.jorotayo.algorubickrevamped.data.AppSettings
import com.jorotayo.algorubickrevamped.data.SettingsRepository
import com.jorotayo.algorubickrevamped.ui.settings.PracticeFeedbackPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    application: Application,
    private val mode: StudyMode,
    private val algorithmIds: List<Long>,
    private val algorithmRepository: AlgorithmRepository = AlgorithmRepository(),
    private val settingsRepository: SettingsRepository = SettingsRepository.get(application),
) : AndroidViewModel(application) {

    private val feedbackPlayer = PracticeFeedbackPlayer(application)
    private val algorithms: List<Algorithm>
    private val session: MutableList<Algorithm> = mutableListOf()
    private var sessionPosition = 0
    private val sessionAttempts = mutableMapOf<Long, Int>()
    private val sessionCorrects = mutableMapOf<Long, Int>()
    private var overallCorrect = 0
    private var overallPracticed = 0
    private var timerJob: Job? = null
    private var timerStart = 0L
    private var cachedSettings: AppSettings = AppSettings()

    private val _uiState = MutableStateFlow(StudyAlgorithmUiState(mode = mode))
    val uiState: StateFlow<StudyAlgorithmUiState> = _uiState.asStateFlow()

    init {
        algorithms = algorithmIds.mapNotNull { algorithmRepository.get(it) }
        viewModelScope.launch {
            settingsRepository.settings.collect { cachedSettings = it }
        }
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
        if (correct) {
            feedbackPlayer.playCorrect(cachedSettings)
        } else {
            feedbackPlayer.playWrong(cachedSettings)
        }
        _uiState.update {
            it.copy(
                feedbackTitleRes = if (correct) {
                    R.string.algorithmStudy_feedback_correct_title
                } else {
                    R.string.algorithmStudy_feedback_incorrect_title
                },
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
            overallPracticed++
            if (correct) overallCorrect++
            sessionAttempts[current.id] = (sessionAttempts[current.id] ?: 0) + 1
            if (correct) {
                sessionCorrects[current.id] = (sessionCorrects[current.id] ?: 0) + 1
            }
            current.practiced_number_int += 1
            if (correct) current.practiced_correctly_int += 1
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
            viewModelScope.launch { applySessionLearntFlags() }
            _uiState.update { it.copy(sessionFinished = true) }
            return
        }
        val next = session[sessionPosition++]
        bindAlgorithm(next)
        _uiState.update {
            it.copy(
                input = "",
                correctCount = overallCorrect,
                practicedCount = overallPracticed,
            )
        }
        restartTimer()
    }

    private suspend fun applySessionLearntFlags() {
        val threshold = settingsRepository.settings.first().learntThresholdPercent
        sessionAttempts.forEach { (id, attempts) ->
            if (attempts < 3) return@forEach
            val correct = sessionCorrects[id] ?: 0
            if (correct * 100 / attempts < threshold) return@forEach
            val alg = algorithmRepository.get(id) ?: return@forEach
            if (!alg.learnt) {
                alg.learnt = true
                algorithmRepository.put(alg)
            }
        }
    }

    private fun bindAlgorithm(algorithm: Algorithm) {
        _uiState.update {
            it.copy(
                current = algorithm,
                stepIcons = AlgMoveImages.stepDrawables(algorithm.alg),
                correctCount = if (mode == StudyMode.Practice) overallCorrect else algorithm.practiced_correctly_int,
                practicedCount = if (mode == StudyMode.Practice) overallPracticed else algorithm.practiced_number_int,
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
        fun factory(mode: StudyMode, algorithmIds: List<Long>): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    throw IllegalStateException("Use create(Class, CreationExtras) with Application")
                }

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: androidx.lifecycle.viewmodel.CreationExtras,
                ): T {
                    val app = checkNotNull(
                        extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY],
                    ) as Application
                    return StudyAlgorithmViewModel(app, mode, algorithmIds) as T
                }
            }
    }
}
