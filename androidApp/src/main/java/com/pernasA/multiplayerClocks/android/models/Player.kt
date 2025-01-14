package com.pernasA.multiplayerClocks.android.models

import androidx.compose.ui.graphics.Color

data class Player(
    val name: String,
    val color: Color,
    var totalTimeInSeconds: Int = 0,
    var timePerMoveInSeconds: Int = 0,
    var incrementTimeInSeconds: Int = 0
)