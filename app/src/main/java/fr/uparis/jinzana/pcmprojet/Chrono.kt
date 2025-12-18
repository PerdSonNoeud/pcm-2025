package fr.uparis.jinzana.pcmprojet

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@Composable
fun Chrono(
        minutes: Int,
        finDePartie: (Boolean) -> Unit
    ) {
        var secondsLeft by rememberSaveable { mutableIntStateOf(minutes * 60) }

        // LaunchedEffect pour démarrer le chrono
        LaunchedEffect(key1 = Unit) {
            while (secondsLeft > 0) {
                delay(1000L) // attend 1 seconde
                secondsLeft -= 1
            }
            // Temps écoulé
            finDePartie(true)
        }

        // Affichage mm:ss
        val minutesDisplay = secondsLeft / 60
        val secondsDisplay = secondsLeft % 60
        Text(
            text = String.format("%02d:%02d", minutesDisplay, secondsDisplay),
            fontSize = 24.sp,
            color = Color.Red
        )
    }
