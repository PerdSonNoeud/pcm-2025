package fr.uparis.jinzana.pcmprojet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ScoreViewModelFactory(
    private val dao: ScoreDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScoreViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
