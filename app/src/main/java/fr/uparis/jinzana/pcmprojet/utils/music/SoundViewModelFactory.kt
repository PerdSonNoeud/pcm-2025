package fr.uparis.jinzana.pcmprojet.utils.music

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SoundViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SoundViewModel(context.applicationContext) as T
    }
}
