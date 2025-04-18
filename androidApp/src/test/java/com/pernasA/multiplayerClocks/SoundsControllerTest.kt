package com.pernasA.multiplayerClocks

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import com.pernasA.multiplayerClocks.android.viewModel.SoundsController
import com.pernasA.multiplayerclock.android.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever

class SoundsControllerTest {

    private lateinit var app: Application
    private lateinit var context: Context
    private lateinit var viewModel: SoundsController
    private lateinit var mediaPlayerMock: MediaPlayer

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        app = mock(Application::class.java).apply {
            whenever(applicationContext).thenReturn(context)
        }
        viewModel = spy(SoundsController(app))
        mediaPlayerMock = mock(MediaPlayer::class.java)
    }

    @Test
    fun `toggleSound should change sound state and play button tick`() {
        doNothing().whenever(viewModel).playButtonTickSound()
        val initialState = viewModel.soundsEnabled.value
        viewModel.toggleSound()
        assertNotEquals(initialState, viewModel.soundsEnabled.value)
        verify(viewModel).playButtonTickSound()
    }

    @Test
    fun `playButtonTickSound should create and start MediaPlayer when enabled`() {
        mockStatic(MediaPlayer::class.java).use { mediaPlayerStatic ->
            mediaPlayerStatic.`when`<MediaPlayer> {
                MediaPlayer.create(eq(context), eq(R.raw.button_change_page))
            }.thenReturn(mediaPlayerMock)

            viewModel.playButtonTickSound()

            verify(mediaPlayerMock).setVolume(0.2f, 0.2f)
            verify(mediaPlayerMock).start()
        }
    }

    @Test
    fun `playChangePlayerSound should stop previous and start new one when enabled`() {
        mockStatic(MediaPlayer::class.java).use { mediaPlayerStatic ->
            mediaPlayerStatic.`when`<MediaPlayer> {
                MediaPlayer.create(eq(context), eq(R.raw.change_player_sound))
            }.thenReturn(mediaPlayerMock)

            viewModel.playChangePlayerSound()

            verify(mediaPlayerMock).start()
        }
    }

    @Test
    fun `playTenSecondsLeft should start sound if enabled`() {
        mockStatic(MediaPlayer::class.java).use { mediaPlayerStatic ->
            mediaPlayerStatic.`when`<MediaPlayer> {
                MediaPlayer.create(eq(context), eq(R.raw.less_than_10_seconds))
            }.thenReturn(mediaPlayerMock)

            viewModel.playTenSecondsLeft()

            verify(mediaPlayerMock).start()
        }
    }

    @Test
    fun `playYouLoseGameSound should create and start MediaPlayer`() {
        mockStatic(MediaPlayer::class.java).use { mediaPlayerStatic ->
            mediaPlayerStatic.`when`<MediaPlayer> {
                MediaPlayer.create(eq(context), eq(R.raw.you_lose_sound))
            }.thenReturn(mediaPlayerMock)

            viewModel.playYouLoseGameSound()

            verify(mediaPlayerMock).setVolume(1.0f, 1.0f)
            verify(mediaPlayerMock).start()
        }
    }

    @Test
    fun `playYouWinGameSound should create and start MediaPlayer`() {
        mockStatic(MediaPlayer::class.java).use { mediaPlayerStatic ->
            mediaPlayerStatic.`when`<MediaPlayer> {
                MediaPlayer.create(eq(context), eq(R.raw.win_sound))
            }.thenReturn(mediaPlayerMock)

            viewModel.playYouWinGameSound()

            verify(mediaPlayerMock).setVolume(1.0f, 1.0f)
            verify(mediaPlayerMock).start()
        }
    }

    @Test
    fun `playConfigurationMenuSound should create and start MediaPlayer`() {
        mockStatic(MediaPlayer::class.java).use { mediaPlayerStatic ->
            mediaPlayerStatic.`when`<MediaPlayer> {
                MediaPlayer.create(eq(context), eq(R.raw.configuration_menu))
            }.thenReturn(mediaPlayerMock)

            viewModel.playConfigurationMenuSound()

            verify(mediaPlayerMock).setVolume(1.0f, 1.0f)
            verify(mediaPlayerMock).start()
        }
    }

    @Test
    fun `playPauseOrPlaySound should create and start MediaPlayer`() {
        mockStatic(MediaPlayer::class.java).use { mediaPlayerStatic ->
            mediaPlayerStatic.`when`<MediaPlayer> {
                MediaPlayer.create(eq(context), eq(R.raw.pause_or_play_sound))
            }.thenReturn(mediaPlayerMock)

            viewModel.playPauseOrPlaySound()

            verify(mediaPlayerMock).setVolume(1.0f, 1.0f)
            verify(mediaPlayerMock).start()
        }
    }
}
