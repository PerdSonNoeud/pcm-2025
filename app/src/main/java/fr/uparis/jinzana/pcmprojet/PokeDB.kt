package fr.uparis.jinzana.pcmprojet

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Pokemon::class], version = 1, exportSchema = false)
abstract class PokeDB: RoomDatabase() {
    abstract fun pokeDao(): PokeDao
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