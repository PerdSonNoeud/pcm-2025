package fr.uparis.jinzana.pcmprojet

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PokeViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PokeViewModel::class.java)) {
            return PokeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}