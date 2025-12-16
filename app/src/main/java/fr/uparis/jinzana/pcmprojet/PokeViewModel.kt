package fr.uparis.jinzana.pcmprojet

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PokeViewModel(application: Application): AndroidViewModel(application) {
    private val pokeDao = PokeDB.getInstance(application).pokeDao()

    fun addAllPokemons() {
        val pokemons = getApplication<Application>().resources.getStringArray(R.array.pokemons)
        viewModelScope.launch(Dispatchers.IO) {
            val count = pokeDao.count()
            if (count > 0) {
                Log.d("PokeViewModel", "Database already populated with $count pokemons.")
            } else {
                var id = 1
                for (pokemon in pokemons) {
                    val poke = Pokemon(id = id, name = pokemon)
                    pokeDao.insert(poke)
                    id++
                }
            }
        }
    }
}