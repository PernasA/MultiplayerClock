package com.pernasA.multiplayerClocks.android.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pernasA.multiplayerClocks.android.models.Player
import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.TIME_ALL_GAME
import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.TIME_EACH_MOVE
import com.pernasA.multiplayerClocks.android.viewModel.SharedViewModel
import com.pernasA.multiplayerclock.android.R

@Composable
fun SelectPlayersPage(
    goToChooseTimerOnClick: () -> Unit,
    sharedViewModel: SharedViewModel,
) {
    var playerCount by remember { mutableIntStateOf(2) }
    val playerNames = remember { mutableStateListOf(*Array(8) { "" }) }
    var selectedTimeMode by remember { mutableIntStateOf(-1) }
    val availableColors = remember {
        mutableStateListOf(
            Color(0xFFFF0000), // Rojo
            Color(0xFF00FF00), // Verde
            Color(0xFFFFFF00), // Amarillo
            Color(0xFF00FFFF), // Cian
            Color(0xFF0000FF), // Azul
            Color(0xFF800080), // Morado
            Color(0xFFFF1493), // Rosa
            Color(0xFFFFA500), // Naranja
            Color(0xFF8B4513), // Marrón
            Color(0xFFFFD700), // Dorado
            Color(0xFF40E0D0),  // Turquesa
            Color(0xFFE91E63), // Rosa brillante
            Color(0xFF673AB7), // Índigo
            Color(0xFF009688), // Verde azulado
            Color(0xFFFF5722), // Naranja brillante
            Color(0xFF795548), // Marrón claro
            Color(0xFF00BCD4), // Cian brillante
        )
    }
    val playerColors = remember {
        mutableStateListOf(*Array(8) { i ->
            if (i < availableColors.size) availableColors.removeAt(0) else Color.Transparent
        })
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Text(
                text = "Seleccioná los jugadores",
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                IconButton(onClick = { if (playerCount > 2) playerCount-- }) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        imageVector = Icons.Filled.Remove,
                        contentDescription = stringResource(R.string.button_decrease_description),
                    )
                }

                Text(
                    text = "$playerCount",
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .wrapContentSize(Alignment.Center)
                )

                IconButton(onClick = { if (playerCount < 8) playerCount++ }) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.button_increase_description)
                    )
                }
            }

            for (i in 0 until playerCount) {
                PlayerRow(
                    index = i,
                    playerName = playerNames[i],
                    onNameChange = { playerNames[i] = it },
                    playerColor = playerColors[i],
                    onColorChange = { playerColors[i] = it },
                    availableColors = availableColors
                )
            }

            Text(
                text = "¿Qué modalidad de tiempo preferís?",
                modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimeModeCard(
                    title = "Tiempo por cada jugada",
                    isSelected = selectedTimeMode == TIME_EACH_MOVE,
                    onClick = { selectedTimeMode = TIME_EACH_MOVE }
                )
                TimeModeCard(
                    title = "Tiempo por el total de la partida",
                    isSelected = selectedTimeMode == TIME_ALL_GAME,
                    onClick = { selectedTimeMode = TIME_ALL_GAME }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val playersList = mutableListOf<Player>()
                    for (i in playerNames.indices) {
                        val name = playerNames[i]
                        val color = playerColors.getOrNull(i) ?: Color.Transparent
                        if (name.isNotEmpty() && name.isNotBlank()) {
                            playersList.add(Player(name, color))
                        }
                    }
                    println(playersList)
                    sharedViewModel.setPlayersList(playersList)
                    sharedViewModel.setTypeOfTimer(selectedTimeMode)

                    goToChooseTimerOnClick()
                },
                enabled = selectedTimeMode != -1 && playerNames.take(playerCount).all { it.isNotBlank() }
            ) {
                Text("CONTINUAR")
            }
        }
    }
}

@Composable
fun PlayerRow(
    index: Int,
    playerName: String,
    onNameChange: (String) -> Unit,
    playerColor: Color,
    onColorChange: (Color) -> Unit,
    availableColors: SnapshotStateList<Color>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            "${index + 1}",
            modifier = Modifier.width(30.dp)
        )
        val keyboardController = LocalSoftwareKeyboardController.current

        TextField(
            value = playerName,
            onValueChange = { newName ->
                if (newName.length <= 10) {
                    onNameChange(newName.replaceFirstChar { it.uppercaseChar() })
                }
            },
            label = { Text("Nombre de jugador*") },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
        )


        Spacer(modifier = Modifier.width(8.dp))

        var expanded by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = playerColor.takeIf { it != Color.Transparent }
                        ?: Color.LightGray,
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = if (playerColor != Color.Transparent) Color.Black else Color.Gray,
                    shape = CircleShape
                )
                .clickable { expanded = true }
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableColors.forEach { color ->
                    DropdownMenuItem(
                        onClick = {
                            if (playerColor != Color.Transparent) {
                                availableColors.add(playerColor)
                            }
                            onColorChange(color)
                            expanded = false
                            availableColors.remove(color)
                        },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(color = color, shape = CircleShape)
                                    .border(
                                        width = 1.dp,
                                        color = Color.Black,
                                        shape = CircleShape
                                    )
                            )
                        },
                        text = { Text("") }
                    )
                }
            }
        }
    }
}

@Composable
fun TimeModeCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(150.dp, 100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(
            if (isSelected) 8.dp else 4.dp
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center
            )
        }
    }
}
