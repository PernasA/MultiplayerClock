package com.pernasA.multiplayerClocks.android.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.SUBTITLE_TEXT_SIZE

// el primary no se usa
// el secondary no se usa
// el terciary es para el appbar
// el primaryContainer es para el fondo de la card
// el secondaryContainer es para los botones del HomePage

val LightColorScheme = lightColorScheme(
    primary = ButtonPrimary,
    secondary = ButtonSecondary,
    tertiary = ButtonTertiary,
    primaryContainer = ButtonPrimaryContainer,
    secondaryContainer = ButtonSecondaryContainer,
    onSurfaceVariant = Color.Black, // Mejor contraste sobre fondo blanco
)

@Composable
fun MyClocksAppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = SUBTITLE_TEXT_SIZE,
            lineHeight = SUBTITLE_TEXT_SIZE,
            letterSpacing = 0.5.sp
        )
    )

    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}