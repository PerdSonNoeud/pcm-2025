package fr.uparis.jinzana.pcmprojet

import androidx.compose.runtime.compositionLocalOf
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 0,
    val musicVolume: Float = 0.5f,
    val soundVolume: Float = 0.5f,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val maxHearts: Int = 5,
    var currentHearts: Int = 5,
    var premium: Boolean = false,
    var lastPlayTime: Long = 0L,
    var notifIsActive: Boolean = true

)

enum class Difficulty { EASY, MEDIUM, HARD }



