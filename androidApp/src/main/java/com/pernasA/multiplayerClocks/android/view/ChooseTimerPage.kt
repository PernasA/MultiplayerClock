package com.pernasA.multiplayerClocks.android.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.BIG_TEXT_SIZE
import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.TIME_ALL_GAME
import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.TIME_EACH_MOVE
import com.pernasA.multiplayerClocks.android.viewModel.SharedViewModel
import java.util.Locale

@Composable
fun ChooseTimerPage(
    goToGamePageOnClick: () -> Unit,
    sharedViewModel: SharedViewModel,
) {
    val typeOfTimer = sharedViewModel.getTypeOfTimer()
    var selectedTimeMinutes by remember { mutableIntStateOf(0) }
    var selectedTimeSeconds by remember { mutableIntStateOf(0) }
    var incrementSeconds by remember { mutableIntStateOf(0) }

    LazyColumn {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (typeOfTimer) {
                        TIME_ALL_GAME -> "Tiempo total para la partida"
                        TIME_EACH_MOVE -> "Tiempo por jugada"
                        else -> "Tipo de temporizador no definido"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                CustomTimePicker(
                    totalMinutes = selectedTimeMinutes,
                    totalSeconds = selectedTimeSeconds,
                    onTimeChange = { newMinutes, newSeconds ->
                        selectedTimeMinutes = newMinutes
                        selectedTimeSeconds = newSeconds
                    }
                )

                if (typeOfTimer == TIME_ALL_GAME) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomTimePickerIncrement(
                        label = "Incremento de tiempo por jugada (opcional)",
                        totalSeconds = incrementSeconds,
                        onTimeChange = { newSeconds ->
                            incrementSeconds = newSeconds
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val playersList = sharedViewModel.getPlayersList().toMutableStateList()
                        val totalSeconds = selectedTimeMinutes * 60 + selectedTimeSeconds
                        val incrementSecondsTotal = incrementSeconds

                        when (typeOfTimer) {
                            TIME_ALL_GAME -> {
                                playersList.forEach {
                                    it.totalTimeInSeconds = totalSeconds
                                    it.incrementTimeInSeconds = incrementSecondsTotal
                                }
                            }
                            TIME_EACH_MOVE -> {
                                playersList.forEach { it.timePerMoveInSeconds = totalSeconds }
                            }
                        }

                        sharedViewModel.setPlayersList(playersList)
                        goToGamePageOnClick()
                    },
                    enabled = selectedTimeMinutes > 0 || selectedTimeSeconds > 0,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Continuar")
                }
            }
        }
    }
}




@Composable
fun CustomTimePicker(
    totalMinutes: Int,
    totalSeconds: Int,
    onTimeChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val minutesTens = totalMinutes / 10
    val minutesUnits = totalMinutes % 10
    val secondsTens = totalSeconds / 10
    val secondsUnits = totalSeconds % 10

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Minutos")
            Spacer(modifier = Modifier.width(24.dp),)
            Text("Segundos")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DigitSelector(
                value = minutesTens,
                onValueChange = { newTens ->
                    val newTotalMinutes = (newTens * 10) + minutesUnits
                    onTimeChange(newTotalMinutes, totalSeconds)
                },
                range = 0..9
            )
            DigitSelector(
                value = minutesUnits,
                onValueChange = { newUnits ->
                    val newTotalMinutes = (minutesTens * 10) + newUnits
                    onTimeChange(newTotalMinutes, totalSeconds)
                },
                range = 0..9
            )

            Text(
                modifier = Modifier.width(24.dp),
                text = ":",
                style = TextStyle(fontSize = BIG_TEXT_SIZE)
            )

            DigitSelector(
                value = secondsTens,
                onValueChange = { newTens ->
                    val newTotalSeconds = (newTens * 10) + secondsUnits
                    onTimeChange(totalMinutes, newTotalSeconds)
                },
                range = 0..5 // Máximo 5 para las decenas de segundos
            )
            DigitSelector(
                value = secondsUnits,
                onValueChange = { newUnits ->
                    val newTotalSeconds = (secondsTens * 10) + newUnits
                    onTimeChange(totalMinutes, newTotalSeconds)
                },
                range = 0..9
            )
        }

        Text(
            text = String.format(
                Locale.US,
                "Tiempo: %02d:%02d",
                totalMinutes,
                totalSeconds
            ),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun CustomTimePickerIncrement(
    label: String,
    totalSeconds: Int,
    onTimeChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val secondsTens = totalSeconds / 10
    val secondsUnits = totalSeconds % 10

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DigitSelector(
                value = secondsTens,
                onValueChange = { newTens ->
                    val newTotalSeconds = (newTens * 10) + secondsUnits
                    onTimeChange(newTotalSeconds)
                },
                range = 0..9
            )

            DigitSelector(
                value = secondsUnits,
                onValueChange = { newUnits ->
                    val newTotalSeconds = (secondsTens * 10) + newUnits
                    onTimeChange(newTotalSeconds)
                },
                range = 0..9
            )
        }
        Text(
            text = String.format(
                Locale.US,
                "Incremento: %02d segundos",
                totalSeconds
            ),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun DigitSelector(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(8.dp)
    ) {
        Button(
            onClick = { if (value < range.last) onValueChange(value + 1) },
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Text("▲")
        }

        // Valor actual
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Botón para decrementar
        Button(
            onClick = { if (value > range.first) onValueChange(value - 1) },
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text("▼")
        }
    }
}
