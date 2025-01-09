package com.pernasA.multiplayerClocks.android.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pernasA.multiplayerClocks.android.models.Player

@Composable
fun ChooseTimerPage(
    chooseTimerOnClick: () -> Unit,
    playersList: List<Player>,
    selectedTimeMode: Int,
) {
    var selectedMinutes by remember { mutableIntStateOf(0) }
    var selectedSeconds by remember { mutableIntStateOf(0) }
    var incrementSeconds by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configurar tiempo de la partida",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (selectedTimeMode == 0) { // Tiempo por jugada
            Text("Modo: Tiempo por jugada")
            TimePicker(
                label = "Tiempo por jugada",
                minutes = selectedMinutes,
                seconds = selectedSeconds,
                onMinutesChange = { selectedMinutes = it },
                onSecondsChange = { selectedSeconds = it }
            )
        } else if (selectedTimeMode == 1) { // Tiempo total con incremento
            Text("Modo: Tiempo total de partida con incremento")
            TimePicker(
                label = "Tiempo total",
                minutes = selectedMinutes,
                seconds = selectedSeconds,
                onMinutesChange = { selectedMinutes = it },
                onSecondsChange = { selectedSeconds = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TimePicker(
                label = "Incremento por jugada",
                minutes = 0,
                seconds = incrementSeconds,
                onMinutesChange = {},
                onSecondsChange = { incrementSeconds = it }
            )
        } else {
            Text("Por favor selecciona un modo válido.")
        }

        Button(
            onClick = {
                chooseTimerOnClick(
                    //(selectedMinutes * 60 + selectedSeconds),
                    //incrementSeconds
                )
            },
            enabled = selectedTimeMode in 0..1 && (selectedMinutes > 0 || selectedSeconds > 0),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Continuar")
        }
    }
}

@Composable
fun TimePicker(
    label: String,
    minutes: Int,
    seconds: Int,
    onMinutesChange: (Int) -> Unit,
    onSecondsChange: (Int) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Minutes picker
            NumberPicker(
                range = 0..59,
                selectedValue = minutes,
                onValueChange = onMinutesChange
            )
            Text(":")
            // Seconds picker
            NumberPicker(
                range = 0..59,
                selectedValue = seconds,
                onValueChange = onSecondsChange
            )
        }
    }
}

@Composable
fun NumberPicker(
    range: IntRange,
    selectedValue: Int,
    onValueChange: (Int) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier
            .height(80.dp)
            .width(100.dp)
    ) {
        items(range.toList()) { value ->
            Text(
                text = value.toString().padStart(2, '0'),
                /*style = if (value == selectedValue) {
                    MaterialTheme.typography.h4.copy(color = MaterialTheme.colorScheme.primary)
                } else {
                    MaterialTheme.typography.body1
                },*/
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { onValueChange(value) },
                textAlign = TextAlign.Center
            )
        }
    }
}

