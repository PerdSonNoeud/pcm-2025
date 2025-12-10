package fr.uparis.jinzana.pcmprojet

import androidx.compose.runtime.compositionLocalOf
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 0,
    val musicVolume: Float = 0.5f,
    val soundVolume: Float = 0.5f,
    val difficulty: Difficulty = Difficulty.MEDIUM
)

enum class Difficulty { EASY, MEDIUM, HARD }



