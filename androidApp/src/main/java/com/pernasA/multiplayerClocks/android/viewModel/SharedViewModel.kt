package com.pernasA.multiplayerClocks.android.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pernasA.multiplayerClocks.android.main.NameOfScreen

import com.pernasA.multiplayerClocks.android.models.Player
import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.TIME_ALL_GAME

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive

open class SharedViewModel : ViewModel() {
    private lateinit var navController: NavHostController

    private val _playersList = MutableStateFlow<List<Player>>(emptyList())
    val playersList: StateFlow<List<Player>> = _playersList.asStateFlow()

    private var _typeOfTimer by mutableIntStateOf(-1)

    private var _timePerMoveInSecondsGlobal by mutableIntStateOf(0)
    private var _totalTimeGameGlobal by mutableIntStateOf(0)
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

            if (_typeOfTimer == TIME_ALL_GAME) {
                _playersList.value = _playersList.value.toMutableList().apply {
                    this[_currentPlayerIndex] = this[_currentPlayerIndex].copy(
                        totalTimeInSeconds = (
                            this[_currentPlayerIndex].totalTimeInSeconds + _selectedIncrementTime
                        ).coerceAtLeast(0)
                    )
                }
            } else {
                _playersList.value = _playersList.value.toMutableList().apply {
                    this[_currentPlayerIndex] = this[_currentPlayerIndex].copy(
                        timePerMoveInSeconds = _timePerMoveInSecondsGlobal
                    )
                }
            }

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

                if (_typeOfTimer == TIME_ALL_GAME) {
                    _playersList.value = _playersList.value.toMutableList().apply {
                        this[_currentPlayerIndex] = this[_currentPlayerIndex].copy(
                            totalTimeInSeconds = (this[_currentPlayerIndex].totalTimeInSeconds - 1)
                                .coerceAtLeast(0)
                        )
                    }

                    println("Tiempo actualizado todo el game: ${_playersList.value[_currentPlayerIndex].totalTimeInSeconds}")
                } else {
                    _playersList.value = _playersList.value.toMutableList().apply {
                        this[_currentPlayerIndex] = this[_currentPlayerIndex].copy(
                            timePerMoveInSeconds = (this[_currentPlayerIndex].timePerMoveInSeconds - 1)
                                .coerceAtLeast(0)
                        )
                    }
                    println("Tiempo actualizado por jugada: ${_playersList.value[_currentPlayerIndex].timePerMoveInSeconds}")
                }
            }
        }
    }


    fun setPlayersList(list: List<Player>) {
        _isRunning.value = false
        _playersList.value = list
        _currentPlayerIndex = 0
    }

    open fun getTypeOfTimer(): Int = _typeOfTimer

    fun setTypeOfTimer(type: Int) {
        _isRunning.value = false
        _typeOfTimer = type
    }

    fun setTimePerMoveInSecondsGlobal(time: Int) { _timePerMoveInSecondsGlobal = time }
    fun setTotalTimeGameGlobal(time: Int) { _totalTimeGameGlobal = time }

    fun setSelectedIncrementTime(incrementTime: Int) { _selectedIncrementTime = incrementTime }

    //TOOLBAR TOOLS
    fun resetTimers() {
        for (player in _playersList.value) {
            player.timePerMoveInSeconds = _timePerMoveInSecondsGlobal
            player.totalTimeInSeconds = _totalTimeGameGlobal
        }
        _currentPlayerIndex = 0
    }

    fun saveGame(context: Context) {
        val sharedPreferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val playersJson = gson.toJson(_playersList.value)

        editor.putString("playersList", playersJson)
        editor.putInt("currentPlayerIndex", _currentPlayerIndex)
        editor.putInt("typeOfTimer", _typeOfTimer)
        editor.putInt("timePerMoveInSecondsGlobal", _timePerMoveInSecondsGlobal)
        editor.putInt("totalTimeGameGlobal", _totalTimeGameGlobal)
        editor.putInt("selectedIncrementTime", _selectedIncrementTime)

        editor.apply()
    }

    fun loadGame(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

        val gson = Gson()
        val playersJson = sharedPreferences.getString("playersList", "[]")
        val type = object : TypeToken<List<Player>>() {}.type
        val savedPlayers: List<Player> = gson.fromJson(playersJson, type)

        _playersList.value = savedPlayers
        _currentPlayerIndex = sharedPreferences.getInt("currentPlayerIndex", 0)
        _typeOfTimer = sharedPreferences.getInt("typeOfTimer", -1)
        _timePerMoveInSecondsGlobal = sharedPreferences.getInt("timePerMoveInSecondsGlobal", 0)
        _totalTimeGameGlobal = sharedPreferences.getInt("totalTimeGameGlobal", 0)
        _selectedIncrementTime = sharedPreferences.getInt("selectedIncrementTime", 0)

        return savedPlayers.isNotEmpty() && _typeOfTimer != -1
    }


    fun setNavController(navController: NavHostController) {
        this.navController = navController
    }

    fun goToMenuPage() {
        navController.navigate(NameOfScreen.StartNav.name) {
            popUpTo(NameOfScreen.StartNav.name) { inclusive = true }
        }
        _currentPlayerIndex = 0
    }
}
