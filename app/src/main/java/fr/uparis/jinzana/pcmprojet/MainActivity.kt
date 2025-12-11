package fr.uparis.jinzana.pcmprojet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()

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
}





@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PCMProjetTheme {
       // Greeting(modifier = Modifier.padding())
    }
}