package com.pernasA.multiplayerClocks.android.view

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
    val gameOver = viewModel.gameOver.collectAsState()

    val cardHeight by animateDpAsState(
        targetValue = if (isCurrentPlayer) 180.dp else 140.dp,
        animationSpec = tween(durationMillis = 300), label = "cardHeight"
    )

    val animatedBorder by animateDpAsState(
        targetValue = if (isCurrentPlayer) 8.dp else 5.dp,
        animationSpec = tween(durationMillis = 300), label = "animatedBorder"
    )

    val animatedFontSize by animateDpAsState(
        targetValue = if (isCurrentPlayer) 22.dp else 18.dp,
        animationSpec = tween(durationMillis = 300), label = "animatedFontSize"
    )

    val backgroundColor = if (isCurrentPlayer) player.color.copy(alpha = 0.2f) else player.color.copy(alpha = 0.05f)

    Card(
        modifier = Modifier
            .padding(16.dp)
            .width(200.dp)
            .height(cardHeight)
            .clickable(enabled = !gameOver.value) {
                if (isCurrentPlayer) {
                    if (isRunning.value) {
                        viewModel.getSoundsController().stopTenSecondsLeftSound()
                        viewModel.getSoundsController().playChangePlayerSound()
                        viewModel.switchToNextPlayer()
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Debe tocar el botón play para continuar")
                        }
                    }
                }
            },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(animatedBorder, colorPlayer),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = (index + 1).toString(),
                    color = Color.Black,
                    fontSize = animatedFontSize.value.sp,
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
                    fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            HorizontalDivider(thickness = 3.dp, color = colorPlayer)

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    fontSize = 28.sp,
                    lineHeight = 28.sp

                )
            }
        }
    }
}
