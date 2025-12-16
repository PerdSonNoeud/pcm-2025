package fr.uparis.jinzana.pcmprojet

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pokemon(
    @PrimaryKey(autoGenerate = false) val id: Int = 0,
    val name: String
    )

