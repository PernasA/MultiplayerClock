package com.pernasA.multiplayerClocks.android.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pernasA.multiplayerClocks.android.models.Player
import com.pernasA.multiplayerClocks.android.viewModel.SharedViewModel
import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.TIME_EACH_MOVE
import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.TITLE_TEXT_SIZE
import java.util.Locale

@Composable
fun GamePage(sharedViewModel: SharedViewModel) {
    Scaffold(
        topBar = { GamePageToolbar() },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PlayerCard(0, sharedViewModel)
                PlayerCard(1, sharedViewModel)
            }
        }
    )
}

@Composable
fun PlayerCard(index: Int, viewModel: SharedViewModel) {
    val player = viewModel.getPlayersList()[index]
    val colorPlayer = player.color

    val timeToDisplay = if (viewModel.getTypeOfTimer() == TIME_EACH_MOVE) {
        player.timePerMoveInSeconds
    } else {
        player.totalTimeInSeconds
    }

    val minutes = timeToDisplay / 60
    val seconds = timeToDisplay % 60
    val formattedTime = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    Card(
        modifier = Modifier
            .padding(16.dp)
            .width(200.dp)
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(3.dp, colorPlayer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
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
                    modifier = Modifier.width(40.dp).padding(horizontal = 15.dp)
                )
                VerticalDivider(thickness = 3.dp, color = colorPlayer)
                Text(
                    text = player.name,
                    color = Color.Black,
                    fontSize = TITLE_TEXT_SIZE,
                    lineHeight = TITLE_TEXT_SIZE,
                    modifier = Modifier.padding(start = 8.dp) // Espaciado entre el divisor y el nombre
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


@Composable
fun GamePageToolbar() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth().height(45.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconsSize = 40.dp

            // Icono Pausa
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { /* Acción para pausar */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Pause,
                    contentDescription = "Pausar",
                    modifier = Modifier.size(iconsSize)
                )
            }

            VerticalDivider(thickness = 3.dp, color = Color.Black)

            // Icono Reanudar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { /* Acción para reanudar */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Reanudar",
                    modifier = Modifier.size(iconsSize)
                )
            }

            VerticalDivider(thickness = 3.dp, color = Color.Black)

            // Icono Configuración
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { /* Acción para abrir configuración */ },
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
        override fun getPlayersList(): List<Player> = listOf(examplePlayer)
        override fun getTypeOfTimer(): Int = 0
    }

    PlayerCard(index = 0, viewModel = fakeViewModel)
}

@Preview(showBackground = true)
@Composable
fun GamePageToolbarPreview() {
    GamePageToolbar()
}

