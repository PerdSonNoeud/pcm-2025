package fr.uparis.jinzana.pcmprojet.utils.music

import android.content.Context
import android.media.SoundPool

class SoundManager(private val context: Context) {

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .build()

    private val sounds = mutableMapOf<String, Int>()

    fun loadSound(key: String, resId: Int) {
        sounds[key] = soundPool.load(context, resId, 1)
    }

    fun playSound(key: String, volume: Float) {
        sounds[key]?.let {
            soundPool.play(it, volume, volume, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
    }
}

