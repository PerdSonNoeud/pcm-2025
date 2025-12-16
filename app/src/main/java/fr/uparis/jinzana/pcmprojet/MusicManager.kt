package fr.uparis.jinzana.pcmprojet

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes

class MusicManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentResId: Int? = null
    private var isLooping: Boolean = true

    fun playMusic(@RawRes resId: Int, volume: Float , loop: Boolean = true) {



        if (mediaPlayer != null && currentResId == resId) {
            mediaPlayer?.setVolume(volume, volume)
            if (!mediaPlayer!!.isPlaying) mediaPlayer?.start()
            return
        }

        stopMusic()

        mediaPlayer = MediaPlayer.create(context, resId).apply {
            isLooping = loop
            setVolume(volume, volume)
            start()
        }

        currentResId = resId
        isLooping = loop
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentResId = null
    }

    fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
}
