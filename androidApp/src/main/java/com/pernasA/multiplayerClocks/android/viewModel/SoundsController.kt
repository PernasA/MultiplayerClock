package com.pernasA.multiplayerClocks.android.viewModel

import android.app.Application
import android.media.MediaPlayer
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.pernasA.multiplayerclock.android.R

class SoundsController (application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private var tickSound: MediaPlayer? = null
    private var changePlayerSound: MediaPlayer? = null
    private var lessThanTenSecondsClockSound: MediaPlayer? = null
    private var youLoseGameSound: MediaPlayer? = null
    private var youWinGameSound: MediaPlayer? = null

    var soundsEnabled = mutableStateOf(true)

    fun toggleSound() {
        soundsEnabled.value = !soundsEnabled.value
    }

    fun playButtonTickSound() {
        if (soundsEnabled.value) {
            stopTickSound()
            tickSound = MediaPlayer.create(context, R.raw.button_change_page)
            tickSound?.start()
        }
    }

    fun stopTickSound() {
        tickSound?.stop()
        tickSound?.release()
        tickSound = null
    }

    fun playChangePlayerSound() {
        if (soundsEnabled.value) {
            stopChangePlayerSound()
            changePlayerSound = MediaPlayer.create(context, R.raw.change_player_sound)
            changePlayerSound?.start()
        }
    }

    fun stopChangePlayerSound() {
        changePlayerSound?.stop()
        changePlayerSound?.release()
        changePlayerSound = null
    }

    fun playTenSecondsLeft() {
        if (soundsEnabled.value) {
            stopTenSecondsLeftSound()
            lessThanTenSecondsClockSound = MediaPlayer.create(context, R.raw.less_than_10_seconds)
            lessThanTenSecondsClockSound?.start()
        }
    }

    fun stopTenSecondsLeftSound() {
        lessThanTenSecondsClockSound?.stop()
        lessThanTenSecondsClockSound?.release()
        lessThanTenSecondsClockSound = null
    }

    fun playYouLoseGameSound() {
        if (soundsEnabled.value) {
            stopYouLoseGameSound()
            youLoseGameSound = MediaPlayer.create(context, R.raw.you_lose_trompeta)
            youLoseGameSound?.start()
        }
    }

    fun stopYouLoseGameSound() {
        youLoseGameSound?.stop()
        youLoseGameSound?.release()
        youLoseGameSound = null
    }

    fun playYouWinGameSound() {
        if (soundsEnabled.value) {
            stopYouWinGameSound()
            youWinGameSound = MediaPlayer.create(context, R.raw.less_than_10_seconds)
            youWinGameSound?.start()
        }
    }

    fun stopYouWinGameSound() {
        youWinGameSound?.stop()
        youWinGameSound?.release()
        youWinGameSound = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTickSound()
        stopYouLoseGameSound()
        stopYouWinGameSound()
        stopTenSecondsLeftSound()
    }
}