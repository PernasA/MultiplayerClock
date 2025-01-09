package com.pernasA.multiplayerClocks.android.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.pernasA.multiplayerclock.android.R

@Composable
fun ZoomableImage(birdImageResId: Int, modifier: Modifier, contentScale: ContentScale) {
    // Variables de estado para el zoom y el desplazamiento
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Restablece el zoom y el desplazamiento al cambiar la imagen
    LaunchedEffect(birdImageResId) {
        scale = 1f
        offset = Offset.Zero
    }

    // Configuración del transform gesture detector para detectar el zoom y el desplazamiento
    Image(
        painter = painterResource(id = birdImageResId),
        contentDescription = stringResource(R.string.image_generic_description),
        contentScale = contentScale,
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // Actualizar el nivel de zoom y el desplazamiento
                    val newScale = (scale * zoom).coerceIn(1f, 5f) // Limita el zoom entre 1x y 5x

                    // Calcular el tamaño de la imagen escalada
                    val maxTranslationX = (newScale - 1) * size.width / 2
                    val maxTranslationY = (newScale - 1) * size.height / 2

                    // Limitar el desplazamiento en función del zoom
                    val newOffsetX = (offset.x + pan.x * newScale).coerceIn(-maxTranslationX, maxTranslationX)
                    val newOffsetY = (offset.y + pan.y * newScale).coerceIn(-maxTranslationY, maxTranslationY)

                    scale = newScale
                    offset = Offset(newOffsetX, newOffsetY)
                }
            }
    )
}