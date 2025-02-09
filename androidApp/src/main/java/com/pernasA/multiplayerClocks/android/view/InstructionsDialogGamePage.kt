package com.pernasA.multiplayerClocks.android.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pernasA.multiplayerClocks.android.viewModel.SharedViewModel

@Composable
fun InstructionsDialogGamePage(
    showInstructionsDialog: MutableState<Boolean>,
    viewModel: SharedViewModel
) {
    AlertDialog(
        onDismissRequest = { showInstructionsDialog.value = false },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color(0xFFF8F9FA),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                "📜 Instrucciones 📜",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }
        },
        text = {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text("1️⃣ Para pasar al siguiente jugador, haz clic en el jugador con el tiempo activo 🎯")
                Spacer(modifier = Modifier.height(8.dp))
                Text("2️⃣ El orden es secuencial: Jugador 1 → Jugador 2 → Jugador 3... y así sucesivamente.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("3️⃣ Si un jugador llega a 0, puedes eliminarlo o finalizar la partida ⏳")
                Spacer(modifier = Modifier.height(8.dp))
                Text("4️⃣ Desde el botón de configuración, hay opciones para continuar 🛠️")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.getSoundsController().playPauseOrPlaySound()
                    showInstructionsDialog.value = false
                          },
            ) {
                Text("¡Entendido! 👍", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}