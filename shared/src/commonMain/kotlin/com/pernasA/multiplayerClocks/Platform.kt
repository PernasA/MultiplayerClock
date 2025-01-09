package com.pernasA.multiplayerClocks

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform