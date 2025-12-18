package fr.uparis.jinzana.pcmprojet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PokeViewModel(application: Application): AndroidViewModel(application) {
    private val pokeDao = PokeDB.getInstance(application).pokeDao()
    val allPokemons: Flow<List<Pokemon>> = pokeDao.getAll()

    fun addPokemon(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            pokeDao.insert(Pokemon(name = name))
        }
    }

}