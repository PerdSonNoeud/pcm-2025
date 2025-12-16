package fr.uparis.jinzana.pcmprojet

import android.app.Application
import androidx.annotation.RawRes
import androidx.lifecycle.AndroidViewModel

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val musicManager = MusicManager(application)

    fun play(@RawRes resId: Int, volume: Float, loop: Boolean = true) {
        musicManager.playMusic(resId, volume, loop)
    }

    fun pause() {
        musicManager.pauseMusic()
    }

    fun stop() {
        musicManager.stopMusic()
    }

    fun setVolume(volume: Float) {
        musicManager.setVolume(volume)
    }

    fun isPlaying() = musicManager.isPlaying()

    override fun onCleared() {
        super.onCleared()
        musicManager.stopMusic()
    }
}
