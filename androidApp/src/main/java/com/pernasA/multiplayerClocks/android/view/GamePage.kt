package com.pernasA.multiplayerClocks.android.view

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn

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
import com.pernasA.multiplayerClocks.android.viewModel.SharedViewModel

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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    players.forEachIndexed { index, player ->
                        PlayerCard(
                            player = player, index = index,
                            viewModel = viewModel,
                            snackbarHostState, coroutineScope
                        )
                    }
                    if (showGameOverDialog.value) {
                        if (remainingPlayers == 2) {

                            // Caso donde solo quedan 2 jugadores y uno pierde
                            AlertDialog(
                                onDismissRequest = {},
                                title = { Text("¡Juego terminado!") },
                                text = { Text("${players[viewModel.currentPlayerIndex].name} se quedó sin tiempo\n " +
                                        "¡¡El ganador es ${players[(viewModel.currentPlayerIndex+1)%2].name}!!") },
                                confirmButton = {
                                    Button(onClick = { viewModel.endGame() }) {
                                        Text("Aceptar")
                                    }
                                }
                            )
                        } else {
                            // Caso normal cuando hay más de 2 jugadores
                            AlertDialog(
                                onDismissRequest = {},
                                title = { Text("¡Tiempo agotado!") },
                                text = { Text("${players[viewModel.currentPlayerIndex].name} se quedó sin tiempo. ¿Qué desean hacer?") },
                                confirmButton = {
                                    Button(onClick = { viewModel.endGame() }) {
                                        Text("Finalizar juego")
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { viewModel.removePlayer(viewModel.currentPlayerIndex) }) {
                                        Text("Eliminar jugador")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamePageToolbar(viewModel: SharedViewModel) {
    val showSettingsDialog = remember { mutableStateOf(false) }
    val isSoundEnabled = remember { mutableStateOf(true) }
    val iconsSize = 40.dp
    val isRunning by viewModel.isRunning.collectAsState()

    Column {
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
                    .clickable { viewModel.togglePauseResume() },
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
        ConfigurationModal(showSettingsDialog, viewModel, isSoundEnabled)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ConfigurationModal(
    showSettingsDialog: MutableState<Boolean>,
    viewModel: SharedViewModel,
    isSoundEnabled: MutableState<Boolean>
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
                    showSettingsDialog.value = false
                    viewModel.goToMenuPage()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ir al menú")
            }

            Spacer(modifier = Modifier.height(16.dp))

            IconButton(onClick = { isSoundEnabled.value = !isSoundEnabled.value }) {
                Icon(
                    imageVector = if (isSoundEnabled.value) Icons.AutoMirrored.Default.VolumeUp else Icons.AutoMirrored.Default.VolumeOff,
                    contentDescription = "Sonido",
                    tint = if (isSoundEnabled.value) Color.Green else Color.Gray
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

