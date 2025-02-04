package com.pernasA.multiplayerClocks.android.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.pernasA.multiplayerClocks.android.models.Player
import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.TIME_EACH_MOVE
import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.TITLE_TEXT_SIZE
import com.pernasA.multiplayerClocks.android.viewModel.SharedViewModel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun PlayerCard(
    player: Player, index: Int,
    viewModel: SharedViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    val colorPlayer = player.color

    val timeToDisplay = if (viewModel.getTypeOfTimer() == TIME_EACH_MOVE) {
        player.timePerMoveInSeconds
    } else {
        player.totalTimeInSeconds
    }

    val minutes = timeToDisplay / 60
    val seconds = timeToDisplay % 60
    val formattedTime = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    val isRunning = viewModel.isRunning.collectAsState()
    val isCurrentPlayer = index == viewModel.currentPlayerIndex

    // Animación de brillo para el jugador activo
    val animatedBorder by animateColorAsState(
        targetValue = colorPlayer,
        //targetValue = if (isCurrentPlayer) Color.Yellow else colorPlayer,
        animationSpec = tween(durationMillis = 500), label = "animatedBorder"
    )

    // Opacidad del fondo (jugador activo con fondo más claro)
    val backgroundColor = if (isCurrentPlayer) Color(0xFFB0B0B0) else Color(0xFFF0F0F0) //TODO

    Card(
        modifier = Modifier
            .padding(16.dp)
            .width(200.dp)
            .height(140.dp)
            .clickable {
                if (isCurrentPlayer) {
                    if (isRunning.value) {
                        viewModel.switchToNextPlayer()
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Debe tocar el botón play para continuar")
                        }
                    }
                }
            },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            if (isCurrentPlayer) 7.dp else 4.dp,
            animatedBorder
        ), // Cambio de borde animado
        colors = CardDefaults.cardColors(containerColor = backgroundColor) // Cambio de fondo dinámico
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = (index + 1).toString(),
                    color = Color.Black,
                    fontSize = TITLE_TEXT_SIZE,
                    lineHeight = TITLE_TEXT_SIZE,
                    fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.width(40.dp).padding(horizontal = 15.dp)
                )
                VerticalDivider(
                    thickness = if (isCurrentPlayer) 5.dp else 3.dp,
                    color = colorPlayer
                )
                Text(
                    text = player.name,
                    color = Color.Black,
                    fontSize = TITLE_TEXT_SIZE,
                    lineHeight = TITLE_TEXT_SIZE,
                    fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            HorizontalDivider(thickness = 3.dp, color = colorPlayer)

            Text(
                text = formattedTime,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black,
                fontSize = 28.sp,
                lineHeight = 28.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp)
            )
        }
    }
}
