import android.content.Context
import fr.uparis.jinzana.pcmprojet.PokeDao
import fr.uparis.jinzana.pcmprojet.Pokemon
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
                        pokemonsToInsert.add(Pokemon(id = id, name = name))
                    }
                }
            }
        }


        pokeDao.insertAll(pokemonsToInsert)
    }
}
