package fr.uparis.jinzana.pcmprojet

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PokeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(pokemons: List<Pokemon>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(pokemon: Pokemon): Long

    @Update
    suspend fun update(pokemon: Pokemon)

    @Delete
    suspend fun delete(pokemon: Pokemon)

    @Query("SELECT * FROM Pokemon")
    fun getAll(): Flow<List<Pokemon>>

    @Query("SELECT * FROM Pokemon WHERE id = :id")
    fun getById(id: Int): Flow<Pokemon?>
}