package fr.uparis.jinzana.pcmprojet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.uparis.jinzana.pcmprojet.utils.settings.DifficultyConverter
import fr.uparis.jinzana.pcmprojet.utils.score.ScoreDao
import fr.uparis.jinzana.pcmprojet.utils.score.ScoreEntity
import fr.uparis.jinzana.pcmprojet.utils.settings.SettingsDao
import fr.uparis.jinzana.pcmprojet.utils.settings.SettingsEntity

@TypeConverters(DifficultyConverter::class)
@Database(entities = [Pokemon::class, SettingsEntity::class, ScoreEntity::class], version = 2, exportSchema = false)
abstract class PokeDB: RoomDatabase() {
    abstract fun pokeDao(): PokeDao
    abstract fun settingsDao(): SettingsDao
    abstract fun scoreDao(): ScoreDao
    companion object {
        @Volatile
        private var dbInstance: PokeDB? = null
        fun getInstance(c: Context): PokeDB {
            if (dbInstance == null) {
                dbInstance =
                    Room.databaseBuilder(c.applicationContext, PokeDB::class.java, "poke.db")
                        .fallbackToDestructiveMigration(true)
                        .build()
            }
            return dbInstance!!
        }
    }
}