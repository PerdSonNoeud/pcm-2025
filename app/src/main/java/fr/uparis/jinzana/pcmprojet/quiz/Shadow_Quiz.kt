package fr.uparis.jinzana.pcmprojet.quiz

import android.app.Application
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.Normalizer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import fr.uparis.jinzana.pcmprojet.utils.settings.Difficulty
import fr.uparis.jinzana.pcmprojet.EndOfGame
import fr.uparis.jinzana.pcmprojet.Pokemon_Police
import fr.uparis.jinzana.pcmprojet.R
import fr.uparis.jinzana.pcmprojet.utils.settings.SettingsViewModel
import fr.uparis.jinzana.pcmprojet.utils.settings.SettingsViewModelFactory
import fr.uparis.jinzana.pcmprojet.utils.music.SoundViewModel
import fr.uparis.jinzana.pcmprojet.utils.music.SoundViewModelFactory
import fr.uparis.jinzana.pcmprojet.database.PokeViewModel
import fr.uparis.jinzana.pcmprojet.database.PokeViewModelFactory
import fr.uparis.jinzana.pcmprojet.database.Pokemon
import fr.uparis.jinzana.pcmprojet.ui.theme.BgColor
import fr.uparis.jinzana.pcmprojet.ui.theme.IdBgColor
import fr.uparis.jinzana.pcmprojet.ui.theme.Primary
import fr.uparis.jinzana.pcmprojet.ui.theme.Secondary
import fr.uparis.jinzana.pcmprojet.utils.Chrono
import fr.uparis.jinzana.pcmprojet.utils.music.PageMusic


private fun String.normalizeForQuiz(): String {
    return Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .lowercase()
}


@Composable
private fun ShadowSprite(pokemonId: Int) {
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
    var hintsCount by rememberSaveable { mutableIntStateOf(0) }
    var hintText by rememberSaveable { mutableStateOf("") }
    var finDePartie by rememberSaveable { mutableStateOf(false) }
    val pokeVM: PokeViewModel = viewModel(
        factory = PokeViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Primary)
    ) {
        val isLandscape = maxWidth > maxHeight
        val pokemons by pokeVM.allPokemons.collectAsState(initial = emptyList())
        val filteredPokemons = remember(input) {
            if (input.length >= 2) {
                pokemons.filter { it.name.startsWith(input, ignoreCase = true) }
            } else {
                emptyList()
            }
        }
        val restants = rememberSaveable { mutableStateListOf<Pokemon>() }
        var pokemonActuelId by rememberSaveable { mutableStateOf<Int?>(null) }

        val pokemonActuel = pokemons.find { it.id == pokemonActuelId }
        PageMusic(R.raw.shadow, settings.musicVolume)
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

        fun pokeResult() {
            val correctName = pokemonActuel?.name ?: ""
            if (input.normalizeForQuiz().equals(correctName.normalizeForQuiz(), ignoreCase=true)) {
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
            EndOfGame(score, "Shadow")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize(.95f)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Secondary)
                    .then(
                        if (isLandscape) Modifier else Modifier.padding(16.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = "Shadow Quiz",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    fontFamily = Pokemon_Police,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Score : $score",
                        fontFamily = Pokemon_Police,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(top = 10.dp, start = 10.dp)
                    )
                    if (hintsCount > 0 && settings.difficulty != Difficulty.HARD && coeurs>=1) {
                        Button(
                            onClick = {
                                hintsCount = 0
                                hintText = ""
                                suivant()
                            },
                            modifier = Modifier
                                .height(25.dp)
                                .width(80.dp)
                                .align(Alignment.Center),
                            contentPadding = PaddingValues(0.dp) ,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFB00020),
                            ),
                        ) {
                            Text("Passer", fontSize = 10.sp, textAlign = TextAlign.Center)
                        }
                    }
                    Text(
                        text = "\uD83D\uDDA4".repeat(initialCoeurs - coeurs) + "❤️".repeat(coeurs),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(top = 10.dp, end = 10.dp)
                    )
                    if(settings.difficulty== Difficulty.HARD  && coeurs >= 1)
                        Chrono(minutes = 3, modifier = Modifier.align(Alignment.Center)) { ended ->
                            finDePartie = ended
                        }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxWidth(.8f)
                        .background(BgColor)
                ) {
                    if (!isLandscape) Spacer(Modifier.height(24.dp))
                    pokemonActuel?.let { pokemon ->
                        ShadowSprite(pokemon.id)
                    }
                    if (!isLandscape) Spacer(Modifier.height(24.dp))
                    OutlinedTextField(
                        value = input,
                        isError = hintsCount > 0,
                        onValueChange = { input = it },
                        label = { Text(
                            text = "Nom du Pokémon",
                            color = Secondary
                        ) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
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
                    if (filteredPokemons.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .heightIn(max = 200.dp)
                                .widthIn(max = 275.dp)
                                .border(1.dp, Color.Gray)
                        ) {
                            items(filteredPokemons) { pokemon ->
                                Text(
                                    text = pokemon.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { input = pokemon.name }
                                        .padding(10.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    val soundVM: SoundViewModel =  viewModel(
                        factory = SoundViewModelFactory(LocalContext.current)
                    )
                    Row {
                        Button(
                            onClick = {
                                pokeResult()
                                soundVM.play("click", settings.soundVolume)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = IdBgColor,
                                contentColor = Color.White
                            ),
                        ) {
                            Text("Valider")
                        }
                    }
                    if (hintText.isNotEmpty()) {
                        Text(
                            text = "Le Pokémon commence par : $hintText",
                            color = Color(0xFFB00020),
                            fontSize = 9.sp,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}