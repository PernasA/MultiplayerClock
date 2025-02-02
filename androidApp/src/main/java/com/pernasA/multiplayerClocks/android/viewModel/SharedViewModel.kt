package com.pernasA.multiplayerClocks.android.viewModel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope

import com.pernasA.multiplayerClocks.android.models.Player

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive

open class SharedViewModel : ViewModel() {
    private val _playersList = MutableStateFlow<List<Player>>(emptyList())
    val playersList: StateFlow<List<Player>> = _playersList.asStateFlow()

    private var _typeOfTimer by mutableIntStateOf(-1)
    private var _selectedTime by mutableIntStateOf(0)
    private var _selectedIncrementTime by mutableIntStateOf(0)

    private var _currentPlayerIndex by mutableIntStateOf(0)
    val currentPlayerIndex: Int get() = _currentPlayerIndex

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimer()
    }

    fun togglePauseResume() {
        println("Se llamó a tooglePauseResume")
        _isRunning.value = !_isRunning.value
        if (_isRunning.value) {
            startTimer()
        } else {
            println("Se pausó el timer. con timerJob.cancel")
            timerJob?.cancel()
        }
    }

    fun togglePause() {
        if (_isRunning.value) {
            println("Se pausó el timer. con timerJob.cancel")
            _isRunning.value = false
            timerJob?.cancel()
        }
    }

    fun switchToNextPlayer() {
        if (_isRunning.value) {
            println("Se llamó a switchToNextPlayer")
            timerJob?.cancel()
            _currentPlayerIndex = (_currentPlayerIndex + 1) % _playersList.value.size
            startTimer()
        }
    }

    private fun startTimer() {
        println("Se llamó a startTimer")
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _isRunning.value) {
                delay(1000)
                _playersList.value = _playersList.value.toMutableList().apply {
                    this[_currentPlayerIndex] = this[_currentPlayerIndex].copy(
                        totalTimeInSeconds = (this[_currentPlayerIndex].totalTimeInSeconds - 1).coerceAtLeast(0)
                    )
                }
                println("Tiempo actualizado: ${_playersList.value[_currentPlayerIndex].totalTimeInSeconds}")
            }
        }
    }


    fun setPlayersList(list: List<Player>) {
        _isRunning.value = false
        _playersList.value = list
    }

    open fun getTypeOfTimer(): Int = _typeOfTimer
    fun setTypeOfTimer(type: Int) {
        _isRunning.value = false
        _typeOfTimer = type
    }

    fun getSelectedTime(): Int = _selectedTime
    fun setSelectedTime(time: Int) { _selectedTime = time }

    fun getSelectedIncrementTime(): Int = _selectedIncrementTime
    fun setSelectedIncrementTime(incrementTime: Int) { _selectedIncrementTime = incrementTime }
}
