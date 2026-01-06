package fr.uparis.jinzana.pcmprojet.utils.score

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.uparis.jinzana.pcmprojet.utils.settings.Difficulty

@Entity(tableName = "scores")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val quizName: String,
    val difficulty: Difficulty,
    val playerTag: String,
    val score: Int
)
