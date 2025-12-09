package fr.uparis.jinzana.pcmprojet

import androidx.compose.runtime.*

data class Settings(
    var musicVolume: Float = 0.5f,
    var soundVolume: Float = 0.5f,
    var difficulty: Difficulty = Difficulty.MEDIUM
)

enum class Difficulty { EASY, MEDIUM, HARD }


val LocalSettings = compositionLocalOf { mutableStateOf(Settings()) }