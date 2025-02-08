package com.pernasA.multiplayerClocks.android.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.pernasA.multiplayerClocks.android.models.Player
import com.pernasA.multiplayerClocks.android.utils.ButtonPrimary
import com.pernasA.multiplayerClocks.android.viewModel.SharedViewModel
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun GamePage(viewModel: SharedViewModel) {
    val players by viewModel.playersList.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val showGameOverDialog = viewModel.showGameOverDialog.collectAsState()
    val remainingPlayers = viewModel.playersList.collectAsState().value.size

    Scaffold( snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
        GamePageToolbar(viewModel) },
        content = { paddingValues ->
            if (players.size <= 4) {
                // Modo de lista normal (uno debajo del otro)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(players.size) { index ->
                        PlayerCard(
                            player = players[index], index = index,
                            viewModel = viewModel,
                            snackbarHostState = snackbarHostState,
                            coroutineScope = coroutineScope
                        )
                    }
                }
            } else {
                // Modo de dos columnas en zig-zag
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp), // Asegura que las tarjetas no tengan espacios adicionales
                    contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                    items(players.size) { index ->
                        val offsetY =
                            if (index % 2 == 0) 0.dp else 110.dp // Ajusta el valor según el tamaño de la card

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .offset(y = offsetY)
                                .padding(vertical = 20.dp)// Desplaza la tarjeta en Y sin afectar la estructura
                        ) {
                            PlayerCard(
                                player = players[index], index = index,
                                viewModel = viewModel,
                                snackbarHostState = snackbarHostState,
                                coroutineScope = coroutineScope
                            )
                        }
                    }
                }
            }

            val konfettiController = remember { mutableStateOf(false) }

            if (showGameOverDialog.value) {
                if (remainingPlayers == 2) {
                    viewModel.getSoundsController().playYouWinGameSound()
                    konfettiController.value = true
                    // Caso donde solo quedan 2 jugadores y uno pierde
                    AlertDialog(
                        onDismissRequest = {},
                        title = { Text("¡Juego terminado!") },
                        text = { Text("${players[viewModel.currentPlayerIndex].name} se quedó sin tiempo\n " +
                                "¡¡El ganador es ${players[(viewModel.currentPlayerIndex+1)%2].name}!!") },
                        confirmButton = {
                            Button(onClick = {
                                konfettiController.value = false
                                viewModel.getSoundsController().playButtonTickSound()
                                viewModel.endGame()
                            }) {
                                Text("Aceptar")
                            }
                        }
                    )
                } else {
                    viewModel.getSoundsController().playYouLoseGameSound()
                    // Caso normal cuando hay más de 2 jugadores
                    AlertDialog(
                        onDismissRequest = {},
                        title = { Text("¡Tiempo agotado!") },
                        text = { Text("${players[viewModel.currentPlayerIndex].name} se quedó sin tiempo. ¿Qué desean hacer?") },
                        confirmButton = {
                            Button(onClick = {
                                viewModel.getSoundsController().playButtonTickSound()
                                viewModel.endGame()
                            }) {
                                Text("Finalizar juego")
                            }
                        },
                        dismissButton = {
                            Button(onClick = {
                                viewModel.removePlayer(viewModel.currentPlayerIndex)
                                viewModel.getSoundsController().playButtonTickSound()
                            }) {
                                Text("Eliminar jugador")
                            }
                        }
                    )
                }
            }

            if (konfettiController.value) {
                KonfettiView(
                    modifier = Modifier.fillMaxSize(),
                    parties = listOf(
                        Party(
                            speed = 10f,
                            maxSpeed = 30f,
                            damping = 0.9f,
                            angle = 270,
                            spread = 90,
                            shapes = listOf(
                                nl.dionsegijn.konfetti.core.models.Shape.Circle,
                                nl.dionsegijn.konfetti.core.models.Shape.Square
                            ),
                            timeToLive = 3000L,
                            emitter = Emitter(duration = 3, TimeUnit.SECONDS)
                                .perSecond(100)
                        )
                    )
                )
            }
        }
    )
}

@Composable
fun GamePageToolbar(viewModel: SharedViewModel) {
    val showSettingsDialog = remember { mutableStateOf(false) }
    val soundsEnabled = viewModel.getSoundsController().soundsEnabled

    val iconsSize = 40.dp
    val isRunning by viewModel.isRunning.collectAsState()

    Column (Modifier.padding(bottom = 10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono Pausa / Reanudar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        viewModel.getSoundsController().playPauseOrPlaySound()
                        viewModel.togglePauseResume()
                               },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = "Pausar/Reanudar",
                    modifier = Modifier.size(iconsSize)
                )
            }

            VerticalDivider(thickness = 3.dp, color = Color.Black)

            // Icono Configuración
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        viewModel.togglePause()
                        viewModel.getSoundsController().playConfigurationMenuSound()
                        showSettingsDialog.value = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Configuración",
                    modifier = Modifier.size(iconsSize)
                )
            }
        }
        HorizontalDivider(thickness = 3.dp, color = Color.Black)
    }

    if (showSettingsDialog.value) {
        ConfigurationModal(showSettingsDialog, viewModel, soundsEnabled)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ConfigurationModal(
    showSettingsDialog: MutableState<Boolean>,
    viewModel: SharedViewModel,
    soundsEnabled: MutableState<Boolean>
) {
    ModalBottomSheet(
        onDismissRequest = { showSettingsDialog.value = false },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón Reiniciar timers
            Button(
                onClick = {
                    viewModel.getSoundsController().playButtonTickSound()
                    viewModel.resetTimers()
                    showSettingsDialog.value = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reiniciar partida")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Guardar partida
            val localContext = LocalContext.current
            Button(
                onClick = {
                    viewModel.getSoundsController().playButtonTickSound()
                    viewModel.saveGame(localContext)
                    showSettingsDialog.value = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar partida")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.getSoundsController().playButtonTickSound()
                    showSettingsDialog.value = false
                    viewModel.goToMenuPage()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ir al menú")
            }

            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = {
                    viewModel.getSoundsController().toggleSound()
                },
                modifier = Modifier
                    .size(60.dp) // Tamaño del icono aumentado
                    .background(
                        color = if (soundsEnabled.value) ButtonPrimary else Color.Gray,
                        shape = CircleShape
                    )
                    .padding(10.dp) // Espaciado alrededor del icono
            ) {
                Icon(
                    imageVector = if (soundsEnabled.value) Icons.AutoMirrored.Default.VolumeUp else Icons.AutoMirrored.Default.VolumeOff,
                    contentDescription = "Sonido",
                    tint = Color.White
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PlayerCardPreview() {
    val examplePlayer = Player(
        name = "John Doe",
        color = Color.Blue,
        totalTimeInSeconds = 300,
        timePerMoveInSeconds = 30
    )

    val fakeViewModel = object : SharedViewModel() {
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    PlayerCard(viewModel = fakeViewModel, player = examplePlayer, index = 0,
        snackbarHostState = snackbarHostState, coroutineScope = coroutineScope
    )
}

@Preview(showBackground = true)
@Composable
fun GamePageToolbarPreview() {
    val examplePlayer = Player(
        name = "John Doe",
        color = Color.Blue,
        totalTimeInSeconds = 300,
        timePerMoveInSeconds = 30
    )
    val fakeViewModel = object : SharedViewModel() {
        fun getPlayersList(): List<Player> = listOf(examplePlayer)
        override fun getTypeOfTimer(): Int = 0
    }
    GamePageToolbar(fakeViewModel)
}

