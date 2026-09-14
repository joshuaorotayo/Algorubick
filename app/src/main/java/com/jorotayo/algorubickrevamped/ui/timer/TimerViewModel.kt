package com.jorotayo.algorubickrevamped.ui.timer

import android.app.Application
import android.content.Context
import android.text.format.DateFormat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jorotayo.algorubickrevamped.R
import com.jorotayo.algorubickrevamped.data.Solve
import com.jorotayo.algorubickrevamped.data.SolveRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale
import java.util.Random

data class TimerStats(
    val count: String = "NA",
    val best: String = "NA",
    val worst: String = "NA",
    val mean: String = "NA",
    val avg5: String = "NA",
    val avg12: String = "NA",
    val avg50: String = "NA",
    val avg100: String = "NA",
)

data class TimerUiState(
    val running: Boolean = false,
    val timeDisplay: String = "00:00.00",
    val isDnf: Boolean = false,
    val scramble: String = "",
    val scrambleAtStop: String = "",
    val cubeSizes: List<String> = emptyList(),
    val selectedCubeSizeIndex: Int = 0,
    val commandBarVisible: Boolean = false,
    val dnfButtonVisible: Boolean = true,
    val plus2ButtonVisible: Boolean = true,
    val deleteButtonVisible: Boolean = true,
    val overlayHidden: Boolean = false,
    val stats: TimerStats = TimerStats(),
    val toastMessage: String? = null,
    val showDeleteConfirm: Boolean = false,
    val showDnfConfirm: Boolean = false,
    val showPlus2Confirm: Boolean = false,
    val showAddCubeSize: Boolean = false,
    val showDeleteCubeSizeConfirm: Boolean = false,
    val pendingDeleteCubeSizeIndex: Int = -1,
)

/**
 * Must expose a single-arg Application constructor for AndroidViewModelFactory / viewModel().
 */
class TimerViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val solveRepository = SolveRepository()
    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val faceMoves = listOf(
        "R", "L", "U", "D", "F", "B",
        "R'", "L'", "U'", "D'", "F'", "B'",
        "R2", "L2", "U2", "D2", "F2", "B2",
    )

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var startTimeMs = 0L
    private var mins = 0
    private var secs = 0
    private var centiseconds = 0
    private var timerJob: Job? = null
    private var scrambleUsedForSolve: String = ""

    init {
        loadCubeSizes()
        createScramble()
        refreshStatistics()
    }

    fun onToastShown() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun toggleTimer() {
        if (_uiState.value.running) stopTimer() else startTimer()
    }

    private fun startTimer() {
        startTimeMs = android.os.SystemClock.uptimeMillis()
        scrambleUsedForSolve = _uiState.value.scramble
        mins = 0
        secs = 0
        centiseconds = 0
        _uiState.update {
            it.copy(
                running = true,
                isDnf = false,
                commandBarVisible = false,
                overlayHidden = true,
                dnfButtonVisible = true,
                plus2ButtonVisible = true,
                deleteButtonVisible = true,
                timeDisplay = "00:00.00",
            )
        }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                val updatedTime = android.os.SystemClock.uptimeMillis() - startTimeMs
                secs = (updatedTime / 1000).toInt()
                mins = secs / 60
                secs %= 60
                centiseconds = ((updatedTime % 1000) / 10).toInt()
                val display = String.format(Locale.US, "%02d:%02d.%02d", mins, secs, centiseconds)
                _uiState.update { it.copy(timeDisplay = display) }
                delay(16)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update {
            it.copy(
                running = false,
                overlayHidden = false,
                commandBarVisible = true,
                deleteButtonVisible = true,
                dnfButtonVisible = true,
                plus2ButtonVisible = true,
                scrambleAtStop = scrambleUsedForSolve,
            )
        }
        createScramble()
    }

    fun createScramble() {
        val scramble = ArrayList<String>()
        var lastLetter = ""
        val random = Random()
        while (scramble.size < 25) {
            val move = faceMoves[random.nextInt(faceMoves.size)]
            if (lastLetter.isNotEmpty() && move[0] == lastLetter[0]) continue
            scramble.add(move)
            lastLetter = move
        }
        _uiState.update { it.copy(scramble = scramble.joinToString(" ")) }
    }

    fun onScrambleClick() {
        createScramble()
        val msg = getApplication<Application>().getString(R.string.timer_toast_new_scramble)
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun requestDeleteSolve() {
        _uiState.update { it.copy(showDeleteConfirm = true) }
    }

    fun dismissDeleteSolve() {
        _uiState.update { it.copy(showDeleteConfirm = false) }
    }

    fun confirmDeleteSolve() {
        mins = 0
        secs = 0
        centiseconds = 0
        _uiState.update {
            it.copy(
                showDeleteConfirm = false,
                timeDisplay = "00:00.00",
                commandBarVisible = false,
                isDnf = false,
            )
        }
    }

    fun requestDnf() {
        _uiState.update { it.copy(showDnfConfirm = true) }
    }

    fun dismissDnf() {
        _uiState.update { it.copy(showDnfConfirm = false) }
    }

    fun confirmDnf() {
        _uiState.update {
            it.copy(
                showDnfConfirm = false,
                timeDisplay = "DNF",
                isDnf = true,
                dnfButtonVisible = false,
            )
        }
    }

    fun requestPlus2() {
        _uiState.update { it.copy(showPlus2Confirm = true) }
    }

    fun dismissPlus2() {
        _uiState.update { it.copy(showPlus2Confirm = false) }
    }

    fun confirmPlus2() {
        secs += 2
        while (secs >= 60) {
            secs -= 60
            mins += 1
        }
        val display = String.format(Locale.US, "%02d:%02d.%02d", mins, secs, centiseconds)
        val msg = getApplication<Application>().getString(R.string.timer_toast_plus2_applied)
        _uiState.update {
            it.copy(
                showPlus2Confirm = false,
                timeDisplay = display,
                plus2ButtonVisible = false,
                toastMessage = msg,
            )
        }
    }

    fun saveSolve() {
        val state = _uiState.value
        val cubeSize = state.cubeSizes.getOrNull(state.selectedCubeSizeIndex) ?: return
        if (cubeSize == addCubeSizeLabel) return

        val date = DateFormat.format("HH:mm:ss dd-MM-yyyy", Date().time)
        val solve = Solve()
        solve.solve_cube_size = cubeSize
        solve.solve_date = date.toString()

        val isDnf = state.isDnf || state.timeDisplay == "DNF"
        val formattedTime = if (isDnf) {
            "DNF"
        } else {
            var stringSecs = secs.toString()
            if (mins > 0 && secs <= 9) stringSecs = "0$secs"
            if (mins == 0) {
                String.format(Locale.US, "%s.%02ds", stringSecs, centiseconds)
            } else {
                String.format(Locale.US, "%dm %s.%02ds", mins, stringSecs, centiseconds)
            }
        }

        solve.solve_time = formattedTime
        solve.solve_milliseconds = if (isDnf) 0 else getMilliseconds(mins, secs, centiseconds)
        solve.solve_scramble = state.scrambleAtStop.ifBlank { scrambleUsedForSolve }
        solveRepository.put(solve)

        val msg = getApplication<Application>().getString(
            R.string.timer_toast_saved_time,
            formattedTime,
            cubeSize,
        )
        _uiState.update {
            it.copy(
                toastMessage = msg,
                commandBarVisible = false,
            )
        }
        refreshStatistics()
    }

    fun selectCubeSize(index: Int) {
        val sizes = _uiState.value.cubeSizes
        if (index !in sizes.indices) return
        val selected = sizes[index]
        if (selected == addCubeSizeLabel) {
            _uiState.update { it.copy(showAddCubeSize = true) }
            return
        }
        _uiState.update { it.copy(selectedCubeSizeIndex = index) }
        refreshStatistics()
    }

    fun requestDeleteCubeSize(index: Int) {
        val sizes = _uiState.value.cubeSizes
        if (sizes.size <= 1 || index !in sizes.indices) return
        if (sizes[index] == addCubeSizeLabel) return
        _uiState.update {
            it.copy(showDeleteCubeSizeConfirm = true, pendingDeleteCubeSizeIndex = index)
        }
    }

    fun dismissDeleteCubeSize() {
        _uiState.update {
            it.copy(showDeleteCubeSizeConfirm = false, pendingDeleteCubeSizeIndex = -1)
        }
    }

    fun confirmDeleteCubeSize() {
        val index = _uiState.value.pendingDeleteCubeSizeIndex
        val sizes = _uiState.value.cubeSizes.toMutableList()
        if (index in sizes.indices && sizes.size > 1) {
            sizes.removeAt(index)
            persistCubeSizes(sizes)
            _uiState.update {
                it.copy(
                    cubeSizes = sizes,
                    selectedCubeSizeIndex = 0,
                    showDeleteCubeSizeConfirm = false,
                    pendingDeleteCubeSizeIndex = -1,
                )
            }
            refreshStatistics()
        } else {
            dismissDeleteCubeSize()
        }
    }

    fun dismissAddCubeSize() {
        _uiState.update { it.copy(showAddCubeSize = false) }
    }

    fun addCubeSize(newCube: String) {
        val trimmed = newCube.trim()
        if (trimmed.isBlank()) return
        val sizes = _uiState.value.cubeSizes
        val duplicate = sizes.any { it.contains(trimmed, ignoreCase = true) }
        if (duplicate) {
            val msg = getApplication<Application>().getString(
                R.string.timer_toast_cube_size_exists,
                trimmed,
            )
            _uiState.update {
                it.copy(toastMessage = msg, showAddCubeSize = false)
            }
            return
        }
        val updated = sizes.toMutableList()
        val insertAt = (updated.size - 1).coerceAtLeast(0)
        updated.add(insertAt, trimmed)
        persistCubeSizes(updated)
        _uiState.update {
            it.copy(cubeSizes = updated, showAddCubeSize = false)
        }
    }

    fun refreshStatistics() {
        val state = _uiState.value
        val na = getApplication<Application>().getString(R.string.common_not_applicable)
        val cubeSize = state.cubeSizes.getOrNull(state.selectedCubeSizeIndex) ?: run {
            _uiState.update { it.copy(stats = TimerStats()) }
            return
        }
        if (cubeSize == addCubeSizeLabel) {
            _uiState.update { it.copy(stats = TimerStats()) }
            return
        }

        val solves = try {
            solveRepository.byCubeSize(cubeSize)
        } catch (_: Exception) {
            emptyList()
        }

        if (solves.isEmpty()) {
            _uiState.update {
                it.copy(
                    stats = TimerStats(
                        count = na, best = na, worst = na, mean = na,
                        avg5 = na, avg12 = na, avg50 = na, avg100 = na,
                    ),
                )
            }
            return
        }

        val nonDnf = solves.filter { it.solve_time != "DNF" }
        val times = nonDnf.map { it.solve_milliseconds }
        val countLabel = getApplication<Application>().getString(
            R.string.timer_stats_solve_count,
            solves.size,
        )

        _uiState.update {
            it.copy(
                stats = TimerStats(
                    count = countLabel,
                    best = times.minOrNull()?.let(::formatTime) ?: na,
                    worst = times.maxOrNull()?.let(::formatTime) ?: na,
                    mean = if (times.isEmpty()) na else formatTime(times.sum() / times.size),
                    avg5 = averageOfLast(times, 5, na),
                    avg12 = averageOfLast(times, 12, na),
                    avg50 = averageOfLast(times, 50, na),
                    avg100 = averageOfLast(times, 100, na),
                ),
            )
        }
    }

    private fun averageOfLast(times: List<Int>, n: Int, na: String): String {
        if (times.size < n) return na
        val window = times.takeLast(n)
        return formatTime(window.sum() / window.size)
    }

    private fun formatTime(ms: Int): String {
        var seconds = ms / 1000
        val minutes = seconds / 60
        seconds %= 60
        val centis = (ms % 1000) / 10
        return String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, centis)
    }

    private fun getMilliseconds(mins: Int, secs: Int, centiseconds: Int): Int {
        return (mins * 60000) + (secs * 1000) + (centiseconds * 10)
    }

    private fun loadCubeSizes() {
        val cubeSizesSet = prefs.getBoolean(CUBE_SIZES_SET, false)
        val sizes = if (!cubeSizesSet) {
            loadDefaultCubeSizes()
        } else {
            getCubeSizes()
        }
        _uiState.update { it.copy(cubeSizes = sizes, selectedCubeSizeIndex = 0) }
    }

    private fun loadDefaultCubeSizes(): List<String> {
        val defaults = listOf(
            "3x3", "2x2", "4x4", "5x5", "6x6", "7x7", "8x8", "9x9",
            "Megaminx", "Pyraminx", addCubeSizeLabel,
        )
        persistCubeSizes(defaults)
        prefs.edit().putBoolean(CUBE_SIZES_SET, true).apply()
        return defaults
    }

    private fun getCubeSizes(): List<String> {
        val json = prefs.getString(CUBE_SIZES, null) ?: return loadDefaultCubeSizes()
        return try {
            val type = object : TypeToken<ArrayList<String>>() {}.type
            gson.fromJson<ArrayList<String>>(json, type) ?: loadDefaultCubeSizes()
        } catch (_: Exception) {
            loadDefaultCubeSizes()
        }
    }

    private fun persistCubeSizes(sizes: List<String>) {
        prefs.edit().putString(CUBE_SIZES, gson.toJson(sizes)).apply()
    }

    private val addCubeSizeLabel: String
        get() = getApplication<Application>().getString(R.string.timer_add_cube_size_option)

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

    companion object {
        const val PREFS_NAME = "PREFS_NAME"
        const val CUBE_SIZES = "CUBE_SIZES"
        const val CUBE_SIZES_SET = "CUBE_SIZES_SET"
    }
}
