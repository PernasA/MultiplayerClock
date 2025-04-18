package com.pernasA.multiplayerClocks

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import com.pernasA.multiplayerClocks.android.models.Player
import com.pernasA.multiplayerClocks.android.utils.Constants
import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.TIME_EACH_MOVE
import com.pernasA.multiplayerClocks.android.viewModel.SharedViewModel
import com.pernasA.multiplayerClocks.android.viewModel.SoundsController
import io.mockk.mockk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class SharedViewModelTest {

    private lateinit var viewModel: SharedViewModel
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockSoundsController: SoundsController

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockSoundsController = mockk(relaxed = true)
        viewModel = SharedViewModel()
        viewModel.setSoundsController(mockSoundsController)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `togglePauseResume should start timer when not running`() = runTest {
        val players = listOf(Player(name = "A", totalTimeInSeconds = 15, timePerMoveInSeconds = 0, color = Color.Black))
        viewModel.setTypeOfTimer(Constants.TIME_ALL_GAME)
        viewModel.setPlayersList(players)

        viewModel.togglePauseResume()
        assertTrue(viewModel.isRunning.value)

        advanceTimeBy(1000)
        testScheduler.runCurrent()

        assertEquals(14, viewModel.playersList.value[0].totalTimeInSeconds)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `startTimer updates timePerMoveInSeconds and stops the game when arriving at zero`() = runTest {
        val player = Player(timePerMoveInSeconds = 1, totalTimeInSeconds = 10, name = "", color = Color.Black)
        viewModel.setPlayersList(listOf(player))
        viewModel.setTypeOfTimer(TIME_EACH_MOVE)

        viewModel.togglePauseResume()
        advanceTimeBy(1000)
        runCurrent()

        val updatedPlayer = viewModel._playersList.value.first()
        assertEquals(0, updatedPlayer.timePerMoveInSeconds)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `togglePauseResume should pause timer when already running`() = runTest {
        val players = listOf(Player(name = "B", totalTimeInSeconds = 20, timePerMoveInSeconds = 0, color = Color.Black))
        viewModel.setTypeOfTimer(Constants.TIME_ALL_GAME)
        viewModel.setPlayersList(players)

        viewModel.togglePauseResume()
        advanceTimeBy(2000)
        testScheduler.runCurrent()
        viewModel.togglePauseResume()

        assertFalse(viewModel.isRunning.value)
        val currentTime = viewModel.playersList.value[0].totalTimeInSeconds
        advanceTimeBy(2000)
        testScheduler.runCurrent()

        assertEquals(currentTime, viewModel.playersList.value[0].totalTimeInSeconds)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `switchToNextPlayer should increment time if TIME_ALL_GAME`() = runTest {
        val players = listOf(
            Player("A", totalTimeInSeconds = 10, timePerMoveInSeconds = 0, color = Color.Black),
            Player("B", totalTimeInSeconds = 20, timePerMoveInSeconds = 0, color = Color.Black)
        )

        viewModel.setPlayersList(players)
        viewModel.setTypeOfTimer(Constants.TIME_ALL_GAME)
        viewModel.setSelectedIncrementTime(5)

        viewModel.togglePauseResume()
        advanceUntilIdle()
        viewModel.switchToNextPlayer()

        val player0 = viewModel.playersList.value[0]
        assertEquals(0, player0.totalTimeInSeconds)
        assertEquals(0, viewModel.currentPlayerIndex)
    }

    @Test
    fun `removePlayer should end game if last player removed`() = runTest {
        val players = listOf(Player("A", color = Color.Black, 10))
        viewModel.setPlayersList(players)

        viewModel.removePlayer(0)

        assertTrue(viewModel.gameOver.value)
    }

    @Test
    fun `removePlayer should continue if there are more players`() = runTest {
        val players = listOf(
            Player("A", color = Color.Black, 10),
            Player("B", color = Color.Black, 10)
        )
        viewModel.setPlayersList(players)
        viewModel.setTypeOfTimer(Constants.TIME_ALL_GAME)

        viewModel.removePlayer(0)

        assertFalse(viewModel.gameOver.value)
        assertEquals(1, viewModel.playersList.value.size)
        assertEquals("B", viewModel.playersList.value[0].name)
    }

    @Test
    fun `setPlayersList sets the original list and restarts the index`() {
        val players = listOf(Player("Jugador 1", Color.Black), Player("Jugador 2", Color.Black))
        viewModel.setPlayersList(players)

        assertEquals(players, viewModel._playersList.value)
    }

    @Test
    fun `setTypeOfTimer changes the type of timer`() {
        viewModel.setTypeOfTimer(123)

        assertEquals(123, viewModel.getTypeOfTimer())
    }

    @Test
    fun `resetTimers resets the timers and starts the counter`() {
        viewModel.setPlayersList(
            listOf(Player(timePerMoveInSeconds = 0, totalTimeInSeconds = 0, name = "", color = Color.Black))
        )
        viewModel.setTimePerMoveInSecondsGlobal(30)
        viewModel.setTotalTimeGameGlobal(60)

        viewModel.resetTimers()

        val updatedPlayer = viewModel._playersList.value.first()
        assertEquals(30, updatedPlayer.timePerMoveInSeconds)
        assertEquals(60, updatedPlayer.totalTimeInSeconds)
    }

    @Test
    fun `saveGame saves the game correctly in SharedPreferences`() {
        val context = mock(Context::class.java)
        val editor = mock(SharedPreferences.Editor::class.java)
        val sharedPreferences = mock(SharedPreferences::class.java)

        whenever(context.getSharedPreferences(any(), any())).thenReturn(sharedPreferences)
        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.putString(any(), any())).thenReturn(editor)
        whenever(editor.putInt(any(), any())).thenReturn(editor)

        viewModel.setPlayersList(listOf(Player("Test", Color.Black)))
        viewModel.saveGame(context)

        verify(editor).apply()
        verify(editor).putString(any(), any())
    }

    @Test
    fun `loadGame loads the saved state correctly`() {
        val context = mock(Context::class.java)
        val sharedPreferences = mock(SharedPreferences::class.java)

        val players = listOf(Player("Test", Color.Black))
        val gson = Gson()
        val json = gson.toJson(players)

        whenever(context.getSharedPreferences(any(), any())).thenReturn(sharedPreferences)
        whenever(sharedPreferences.getString(eq("playersList"), any())).thenReturn(json)
        whenever(sharedPreferences.getString(eq("originalPlayersList"), any())).thenReturn(json)
        whenever(sharedPreferences.getInt(eq("typeOfTimer"), any())).thenReturn(1)

        val result = viewModel.loadGame(context)

        assertTrue(result)
        assertEquals(players.first().name, viewModel._playersList.value.first().name)
    }
}
