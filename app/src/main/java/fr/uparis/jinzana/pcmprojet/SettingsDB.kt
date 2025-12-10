package fr.uparis.jinzana.pcmprojet

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import kotlin.jvm.java

@Database(entities = [SettingsEntity::class], version = 2)
abstract class SettingsDB : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao


    companion object {
        @Volatile
        private var dbInstance: SettingsDB? = null

        fun getInstance(c: Context): SettingsDB {
            return dbInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    c.applicationContext,
                    SettingsDB::class.java,
                    "app.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                dbInstance = instance
                instance
            }
        }
    }
}
