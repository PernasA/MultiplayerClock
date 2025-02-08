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
    private var configurationMenuSound: MediaPlayer? = null
    private var pauseOrPlaySound: MediaPlayer? = null

    var soundsEnabled = mutableStateOf(true)

    fun toggleSound() {
        soundsEnabled.value = !soundsEnabled.value
        playButtonTickSound()
    }

    fun playButtonTickSound() {
        if (soundsEnabled.value) {
            stopTenSecondsLeftSound()
            stopTickSound()
            tickSound = MediaPlayer.create(context, R.raw.button_change_page)
            tickSound?.setVolume(0.2f, 0.2f)
            tickSound?.start()
        }
    }

    private fun stopTickSound() {
        tickSound?.stop()
        tickSound?.release()
        tickSound = null
    }

    fun playChangePlayerSound() {
        stopTenSecondsLeftSound()
        if (soundsEnabled.value) {
            stopChangePlayerSound()
            changePlayerSound = MediaPlayer.create(context, R.raw.change_player_sound)
            changePlayerSound?.start()
        }
    }

    private fun stopChangePlayerSound() {
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
        stopTenSecondsLeftSound()
        if (soundsEnabled.value) {
            stopYouLoseGameSound()
            youLoseGameSound = MediaPlayer.create(context, R.raw.you_lose_sound)
            youLoseGameSound?.setVolume(1.0f, 1.0f)
            youLoseGameSound?.start()
        }
    }

    private fun stopYouLoseGameSound() {
        youLoseGameSound?.stop()
        youLoseGameSound?.release()
        youLoseGameSound = null
    }

    fun playYouWinGameSound() {
        stopTenSecondsLeftSound()
        if (soundsEnabled.value) {
            stopYouWinGameSound()
            youWinGameSound = MediaPlayer.create(context, R.raw.win_sound)
            youWinGameSound?.setVolume(1.0f, 1.0f)
            youWinGameSound?.start()
        }
    }

    private fun stopYouWinGameSound() {
        youWinGameSound?.stop()
        youWinGameSound?.release()
        youWinGameSound = null
    }

    fun playConfigurationMenuSound() {
        stopTenSecondsLeftSound()
        if (soundsEnabled.value) {
            stopConfigurationMenuSound()
            configurationMenuSound = MediaPlayer.create(context, R.raw.configuration_menu)
            configurationMenuSound?.setVolume(1.0f, 1.0f)
            configurationMenuSound?.start()
        }
    }

    private fun stopConfigurationMenuSound() {
        configurationMenuSound?.stop()
        configurationMenuSound?.release()
        configurationMenuSound = null
    }

    fun playPauseOrPlaySound() {
        stopTenSecondsLeftSound()
        if (soundsEnabled.value) {
            stopPauseOrPlaySound()
            pauseOrPlaySound = MediaPlayer.create(context, R.raw.pause_or_play_sound)
            pauseOrPlaySound?.setVolume(1.0f, 1.0f)
            pauseOrPlaySound?.start()
        }
    }

    private fun stopPauseOrPlaySound() {
        pauseOrPlaySound?.stop()
        pauseOrPlaySound?.release()
        pauseOrPlaySound = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTickSound()
        stopYouLoseGameSound()
        stopYouWinGameSound()
        stopTenSecondsLeftSound()
        stopConfigurationMenuSound()
        stopPauseOrPlaySound()
    }
}