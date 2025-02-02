package com.pernasA.multiplayerClocks.android.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.VerticalDivider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.pernasA.multiplayerClocks.android.models.Player
import com.pernasA.multiplayerClocks.android.viewModel.SharedViewModel

@Composable
fun GamePage(sharedViewModel: SharedViewModel) {
    val players by sharedViewModel.playersList.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold( snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
        GamePageToolbar(sharedViewModel) },
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
                            viewModel = sharedViewModel,
                            snackbarHostState, coroutineScope
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun GamePageToolbar(viewModel: SharedViewModel) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconsSize = 40.dp
            val isRunning by viewModel.isRunning.collectAsState()
            // Icono Pausa / Reanudar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { viewModel.togglePauseResume() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRunning) { Icons.Filled.Pause } else { Icons.Filled.PlayArrow },
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
                        //TODO: abrir modal con información
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
}


@Preview(showBackground = true)
@Composable
fun PlayerCardPreview() {
    // Datos de ejemplo para el Preview
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

