package fr.uparis.jinzana.pcmprojet


import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.jinzana.pcmprojet.Pokemon_Police
import kotlinx.coroutines.launch

@Composable

fun End_of_game(
    score: Int,
    currentQuiz: String
) {
    val Pokemon_Police = FontFamily(
        Font(R.font.pokemonsolid, FontWeight.Bold)
    )

    val backStack = LocalNavBackStack.current
    val context = LocalContext.current
    var playerTag by rememberSaveable { mutableStateOf("") }

    // ViewModels
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context)
    )
    val settings by settingsViewModel.settings.collectAsState()

    val db = PokeDB.getInstance(context)
    val dao = db.scoreDao()
    val scoreViewModel: ScoreViewModel = viewModel(
        factory = ScoreViewModelFactory(dao)
    )

    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = {  },
        title = { Text("Fin de la partie", fontSize = 24.sp,fontFamily = Pokemon_Police) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Score final : $score", fontFamily = fr.uparis.jinzana.pcmprojet.Pokemon_Police)
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = playerTag,
                    onValueChange = { if (it.length <= 3) playerTag = it.uppercase() },
                    label = { Text("Initiales (3 lettres)")  },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (playerTag.length == 3) {
                        scope.launch {
                            scoreViewModel.addScore(
                                quizName = currentQuiz,
                                difficulty = settings.difficulty,
                                playerTag = playerTag,
                                score = score
                            )
                            backStack.remove(backStack.get(1))
                        }
                    } else {
                        Toast.makeText(context, "Entrez 3 lettres", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Finaliser")
            }
        },dismissButton = {
            Button(onClick = { backStack.remove(backStack.get(1)) }) {
                Text("Annuler")
            }
        }
    )
}
