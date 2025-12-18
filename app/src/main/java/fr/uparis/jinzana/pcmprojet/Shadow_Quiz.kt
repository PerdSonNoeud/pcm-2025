package fr.uparis.jinzana.pcmprojet

import android.app.Application
import android.graphics.BitmapFactory

import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.Normalizer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.sqlite.SQLITE_DATA_NULL


fun String.normalizeForQuiz(): String {
    return Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .lowercase()
}


@Composable

fun ShadowSprite(pokemonId: Int) {
    val context = LocalContext.current

    val bitmap = remember(pokemonId) {
        context.assets.open("sprites/$pokemonId.png").use {
            BitmapFactory.decodeStream(it)
        }
    }

    val matrix = remember {
        ColorMatrix(
            floatArrayOf(
                0f, 0f, 0f, 0f, 0f,
                0f, 0f, 0f, 0f, 0f,
                0f, 0f, 0f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        modifier = Modifier.size(200.dp),
        colorFilter =  ColorFilter.colorMatrix(matrix)
    )

}

@Composable
fun Shadow_Quiz(
    modifier: Modifier = Modifier
) {
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current)
    )
    val settings by settingsViewModel.settings.collectAsState()
    var score by rememberSaveable { mutableIntStateOf(0) }
    val initialCoeurs = when (settings.difficulty) {
        Difficulty.EASY -> 5
        Difficulty.MEDIUM -> 3
        Difficulty.HARD -> 1
    }

    var coeurs by rememberSaveable { mutableIntStateOf(initialCoeurs) }

    var input by rememberSaveable { mutableStateOf("") }
    var hintsCount by rememberSaveable { mutableStateOf(0) }
    var hintText by rememberSaveable { mutableStateOf("") }
    var finDePartie by rememberSaveable { mutableStateOf(false) }
    val PokeViewMode: PokeViewModel = viewModel(
        factory = PokeViewModelFactory(LocalContext.current.applicationContext as Application)
    )




    BoxWithConstraints {
        val isLandscape = maxWidth > maxHeight

    val pokemons by PokeViewMode.allPokemons.collectAsState(initial = emptyList())

    val restants = rememberSaveable { mutableStateListOf<Pokemon>() }
    var pokemonActuelId by rememberSaveable { mutableStateOf<Int?>(null) }


    val pokemonActuel = pokemons.find { it.id == pokemonActuelId }



    LaunchedEffect(pokemons) {
        if (pokemons.isNotEmpty() && restants.isEmpty()) {
            val pokesFiltered = if (settings.difficulty == Difficulty.EASY) {
                pokemons.take(649)
            } else {
                pokemons
            }
            restants.clear()
            restants.addAll(pokesFiltered)





            pokemonActuelId = restants.random().id
        }
    }


    fun suivant() {
        restants.remove(pokemonActuel)
        pokemonActuelId = if (restants.isNotEmpty()) restants.random().id else null
        if (pokemonActuel == null) finDePartie = true
        input = ""
    }
        fun PokeResult() {
            val correctName = pokemonActuel?.name ?: ""
            if (input.normalizeForQuiz().equals(correctName.normalizeForQuiz(), ignoreCase = true)) {
                score += 400
                hintsCount = 0
                hintText = ""
                suivant()
            } else if (input.isNotEmpty()) {
                coeurs--
                hintsCount = when (hintsCount) {
                    0 -> 1
                    1 -> 3
                    else -> hintsCount
                }

                hintText = if (correctName.length >= hintsCount) {
                    correctName.take(hintsCount)
                } else correctName

                if (coeurs <= 0) {
                    finDePartie = true
                }
            }
            input = ""
        }

    if (finDePartie) {
        End_of_game(score)

    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (isLandscape) Modifier else Modifier.padding(16.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Score : $score")
            Text("❤️".repeat(coeurs))

            if(settings.difficulty==Difficulty.HARD)
            Chrono (minutes = 3) { ended ->
                finDePartie = ended
            }
        }

        if (!isLandscape) Spacer(Modifier.height(24.dp))


        pokemonActuel?.let { pokemon ->
            ShadowSprite(pokemon.id)
        }

        if (!isLandscape) Spacer(Modifier.height(24.dp))



        OutlinedTextField(
            value = input,
            isError = hintsCount > 0,
            onValueChange = { input = it },
            label = { Text("Nom du Pokémon") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    val correctName = pokemonActuel?.name ?: ""
                    if (input.normalizeForQuiz().equals(correctName.normalizeForQuiz(), ignoreCase = true)) {
                        score += 400
                        hintsCount = 0
                        hintText = ""
                        suivant()
                    } else {
                        coeurs--
                        hintsCount = when (hintsCount) {
                            0 -> 1
                            1 -> 3
                            else -> hintsCount
                        }

                        hintText = if (correctName.length >= hintsCount) {
                            correctName.take(hintsCount)
                        } else correctName

                        if (coeurs <= 0) {
                            finDePartie = true
                        }
                    }
                    input = ""
                }
            )
        )

        Spacer(Modifier.height(12.dp))

        Button(onClick = {
            PokeResult()
        }) {
            Text("Valider")
        }
        if (hintText.isNotEmpty()) {
            Text(
                text = "Le Pokémon commence par : $hintText",
                color = Color(0xFFB00020),
                fontSize = 9.sp,
            )
        }
    }
}
}