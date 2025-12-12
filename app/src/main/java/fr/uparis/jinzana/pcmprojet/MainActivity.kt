package fr.uparis.jinzana.pcmprojet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import fr.uparis.jinzana.pcmprojet.ui.theme.PCMProjetTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    lateinit var soundManager: SoundManager

    lateinit var musicManager: MusicManager
   private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()


        soundManager = SoundManager(this)
        musicManager = MusicManager(this)



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

                    AppNavigation(innerPadding,musicManager)
                }
            }
        }
    }
    override fun onPause() {
        super.onPause()
        musicManager.pauseMusic()
    }

  

}






@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PCMProjetTheme {
       // Greeting(modifier = Modifier.padding())
    }
}