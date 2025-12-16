package fr.uparis.jinzana.pcmprojet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PokedexViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = PokeDB.getInstance(application).pokeDao()
    val pokemons: Flow<List<Pokemon>> = dao.getAll()

    // delete a pokemon from the DB (called from UI on swipe)
    fun delete(pokemon: Pokemon) {
        viewModelScope.launch {
            dao.delete(pokemon)
        }
    }
}