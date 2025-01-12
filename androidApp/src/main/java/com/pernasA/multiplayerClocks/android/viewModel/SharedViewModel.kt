package com.pernasA.multiplayerClocks.android.viewModel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pernasA.multiplayerClocks.android.models.Player

class SharedViewModel : ViewModel() {

    private var _playersList by mutableStateOf<List<Player>>(emptyList())
    private var _typeOfTimer by mutableIntStateOf(-1)
    private var _selectedTime by mutableIntStateOf(0)
    private var _selectedIncrementTime by mutableIntStateOf(0)

    fun setPlayersList(list: List<Player>) {
        _playersList = list
    }

    fun getPlayersList(): List<Player> {
        return _playersList
    }

    fun setTypeOfTimer(type: Int) {
        _typeOfTimer = type
    }

    fun getTypeOfTimer(): Int {
        return _typeOfTimer
    }

    fun setSelectedTime(time: Int) {
        _selectedTime = time
    }

    fun getSelectedTime(): Int {
        return _selectedTime
    }

    fun setSelectedIncrementTime(incrementTime: Int) {
        _selectedIncrementTime = incrementTime
    }

    fun getSelectedIncrementTime(): Int {
        return _selectedIncrementTime
    }
}

