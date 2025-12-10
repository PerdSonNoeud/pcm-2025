package fr.uparis.jinzana.pcmprojet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val dao: SettingsDao) : ViewModel() {


    private val _settings = MutableStateFlow(SettingsEntity())
    val settings: StateFlow<SettingsEntity> = _settings
    private val HEART_RECHARGE_TIME = 10_000L

    init {

        viewModelScope.launch {
            dao.getSettings()?.let { _settings.value = it }
        }
        viewModelScope.launch {
            while (true) {
                delay(1000) // toutes les secondes
                rechargeHeartsIfNeeded() // <-- ici on appelle la fonction
            }
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
    fun updateCurrentHearts(newValue: Int) {
        viewModelScope.launch {
            val updated = _settings.value.copy(currentHearts = newValue)
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
    private suspend fun rechargeHeartsIfNeeded() {
        val settings = _settings.value
        if (settings.currentHearts >= settings.maxHearts) {
            settings.lastPlayTime= System.currentTimeMillis()
            return
        }



        val now = System.currentTimeMillis()
        val elapsed = now - settings.lastPlayTime
        val heartsToAdd = (elapsed / HEART_RECHARGE_TIME).toInt()


        if (heartsToAdd > 0) {
            val newHearts = minOf(settings.currentHearts + heartsToAdd, settings.maxHearts)
            val newLastTime = settings.lastPlayTime + heartsToAdd * HEART_RECHARGE_TIME

            val updated = settings.copy(
                currentHearts = newHearts,
                lastPlayTime = newLastTime
            )

            dao.insertSettings(updated) // sauvegarde dans Room
            _settings.value = updated    // mise à jour StateFlow pour l’UI
        }
    }
    fun timeUntilNextHeart(): Long {
        val s = _settings.value
        if (s.currentHearts >= s.maxHearts) return 0L
        return maxOf((s.lastPlayTime + HEART_RECHARGE_TIME) - System.currentTimeMillis(), 0L)
    }
}
