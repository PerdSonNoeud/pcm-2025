package fr.uparis.jinzana.pcmprojet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import fr.uparis.jinzana.pcmprojet.ui.theme.PCMProjetTheme
import fr.uparis.jinzana.pcmprojet.utils.music.MusicViewModel
import fr.uparis.jinzana.pcmprojet.utils.music.SoundManager
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    lateinit var soundManager: SoundManager
    private val musicViewModel: MusicViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()
        soundManager = SoundManager(this)
        val heartWorker = PeriodicWorkRequestBuilder<HeartNotificationWorker>(15, TimeUnit.MINUTES)
            .build()
        val testWorker = OneTimeWorkRequestBuilder<HeartNotificationWorker>().build()
        WorkManager.getInstance(this).enqueue(testWorker)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "heart_worker",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            heartWorker
        )
        setContent {
            PCMProjetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(innerPadding)
                }
            }
        }
    }
    override fun onPause() {
        super.onPause()
        musicViewModel.pause()
    }
}