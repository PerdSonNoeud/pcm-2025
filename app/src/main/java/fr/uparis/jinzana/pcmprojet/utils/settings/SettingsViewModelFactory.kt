package fr.uparis.jinzana.pcmprojet.utils.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.uparis.jinzana.pcmprojet.database.PokeDB
import kotlin.jvm.java

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            val dao = PokeDB.getInstance(context).settingsDao()
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
