package fr.uparis.jinzana.pcmprojet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow


class PokeViewModel(application: Application): AndroidViewModel(application) {
    private val pokeDao = PokeDB.getInstance(application).pokeDao()
    val allPokemons: Flow<List<Pokemon>> = pokeDao.getAll()
}