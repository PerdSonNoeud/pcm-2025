package fr.uparis.jinzana.pcmprojet.utils

import android.content.Context
import fr.uparis.jinzana.pcmprojet.database.PokeDao
import fr.uparis.jinzana.pcmprojet.database.Pokemon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader


fun importPokemonsFromCSV(context: Context, pokeDao: PokeDao) {
    CoroutineScope(Dispatchers.IO).launch {
        val pokemonsToInsert = mutableListOf<Pokemon>()

        context.assets.open("stat/pokemon.csv").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.lineSequence().drop(1).forEach { line -> // drop header
                    val parts = line.split(",")
                    if (parts.size >= 2) {
                        val id = parts[0].toIntOrNull() ?: 0
                        val name = parts[1]
                        val health = parts[2].toIntOrNull() ?: 0
                        val attack = parts[3].toIntOrNull() ?: 0
                        val defense = parts[4].toIntOrNull() ?: 0
                        val specialAttack = parts[5].toIntOrNull() ?: 0
                        val specialDefense = parts[6].toIntOrNull() ?: 0
                        val speed = parts[7].toIntOrNull() ?: 0
                        pokemonsToInsert.add( Pokemon(
                                id, name, health, attack, defense,
                                specialAttack, specialDefense, speed
                        ) )
                    }
                }
            }
        }
        pokeDao.insertAll(pokemonsToInsert)
    }
}
