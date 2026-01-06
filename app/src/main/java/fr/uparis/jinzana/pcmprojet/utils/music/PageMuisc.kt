package fr.uparis.jinzana.pcmprojet.utils.music

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PageMusic(

    musicResId: Int,
    musicVolume: Float,
    musicManager: MusicViewModel = viewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    musicManager.play(musicResId, musicVolume, true)
    DisposableEffect(lifecycleOwner, musicResId, musicVolume) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> musicManager.play(musicResId, musicVolume, true)
                Lifecycle.Event.ON_PAUSE -> musicManager.pause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)

        }
    }
}
