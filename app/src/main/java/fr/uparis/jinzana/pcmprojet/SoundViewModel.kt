package fr.uparis.jinzana.pcmprojet

import android.content.Context
import androidx.lifecycle.ViewModel

class SoundViewModel(private val appContext: Context) : ViewModel() {

    val soundManager = SoundManager(appContext)

    init {
        loadDefaultSounds()
    }

    private fun loadDefaultSounds() {
        soundManager.loadSound("click", R.raw.click)

    }

    fun play(key: String, volume: Float) {
        soundManager.playSound(key, volume)
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
