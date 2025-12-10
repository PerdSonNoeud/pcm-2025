package fr.uparis.jinzana.pcmprojet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val dao: SettingsDao) : ViewModel() {


    private val _settings = MutableStateFlow(SettingsEntity())
    val settings: StateFlow<SettingsEntity> = _settings

    init {

        viewModelScope.launch {
            dao.getSettings()?.let { _settings.value = it }
        }
    }

    fun updateDifficulty(newDifficulty: Difficulty) {
        viewModelScope.launch {
            val updated = _settings.value.copy(difficulty = newDifficulty)
            dao.insertSettings(updated)
            _settings.value = updated
        }
    }

    fun updateMusicVolume(volume: Float) {
        viewModelScope.launch {
            val updated = _settings.value.copy(musicVolume = volume)
            dao.insertSettings(updated)
            _settings.value = updated
        }
    }

    fun updateSoundVolume(volume: Float) {
        viewModelScope.launch {
            val updated = _settings.value.copy(soundVolume = volume)
            dao.insertSettings(updated)
            _settings.value = updated
        }
    }
}
