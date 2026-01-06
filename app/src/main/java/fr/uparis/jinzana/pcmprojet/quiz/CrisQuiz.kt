package fr.uparis.jinzana.pcmprojet.quiz

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ColorMatrix
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter

import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.jinzana.pcmprojet.utils.settings.Difficulty
import fr.uparis.jinzana.pcmprojet.EndOfGame
import fr.uparis.jinzana.pcmprojet.database.PokeViewModel
import fr.uparis.jinzana.pcmprojet.database.PokeViewModelFactory
import fr.uparis.jinzana.pcmprojet.database.Pokemon
import fr.uparis.jinzana.pcmprojet.Pokemon_Police
import fr.uparis.jinzana.pcmprojet.ui.theme.BgColor
import fr.uparis.jinzana.pcmprojet.ui.theme.IdBgColor
import fr.uparis.jinzana.pcmprojet.ui.theme.Primary
import fr.uparis.jinzana.pcmprojet.ui.theme.Secondary
import fr.uparis.jinzana.pcmprojet.utils.settings.SettingsViewModel
import fr.uparis.jinzana.pcmprojet.utils.settings.SettingsViewModelFactory


@Composable
private fun PokemonSprite(pokemonId: Int, size: Dp = 100.dp) {
    val context = LocalContext.current
    val bitmap = remember(pokemonId) {
        context.assets.open("sprites/$pokemonId.png").use { input ->
            BitmapFactory.decodeStream(input)
        }
    }
    bitmap?.asImageBitmap()?.let { img ->
        Image(
            bitmap = img,
            contentDescription = "Pokemon $pokemonId",
            modifier = Modifier.size(size)
        )
    }
}


@Composable
private fun ShadowSprite(pokemonId: Int, size: Dp = 100.dp) {
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
        modifier = Modifier.size(size),
        colorFilter = ColorFilter.colorMatrix(matrix)
    )
}

fun playCry( context: Context, pokemonId: Int,volume : Float) {
    val afd = context.assets.openFd("cries/$pokemonId.ogg")
    val player = MediaPlayer().apply {
        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        prepare()
        setVolume(volume, volume)
        start()
    }
    player.setOnCompletionListener { it.release() }
}


@Composable
fun CrisQuiz(
    modifier: Modifier = Modifier
) {
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current)
    )
    val settings by settingsViewModel.settings.collectAsState()
    val context = LocalContext.current
    @Composable
    fun PokemonOptionItem(
        pokemon: Pokemon,
        score: Int,
        onClick: () -> Unit
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val shadowChance = minOf(15 + (score / 1000) * 10, 100)
            val isShadow = settings.difficulty == Difficulty.HARD &&
                (0..99).random() < shadowChance

            if (isShadow) {
                ShadowSprite(pokemon.id, size = 100.dp)
            } else {
                PokemonSprite(pokemon.id, size = 100.dp)
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IdBgColor,
                    contentColor = Color.White
                ),
            ) {
                Text(if (isShadow) "?" else pokemon.name)
            }
        }
    }

    var score by rememberSaveable { mutableIntStateOf(0) }
    val initialCoeurs = when (settings.difficulty) {
        Difficulty.EASY -> 5
        Difficulty.MEDIUM -> 3
        Difficulty.HARD -> 1
    }
    var coeurs by rememberSaveable { mutableIntStateOf(initialCoeurs) }
    var finDePartie by rememberSaveable { mutableStateOf(false) }
    var hintsCount by rememberSaveable { mutableIntStateOf(0) }
    val pokeVM: PokeViewModel = viewModel(
        factory = PokeViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val pokemons by pokeVM.allPokemons.collectAsState(initial = emptyList())
    var currentPokemonId by rememberSaveable { mutableIntStateOf(-1) }
    val currentPokemon = pokemons.firstOrNull { it.id == currentPokemonId }
    var options by rememberSaveable { mutableStateOf<List<Pokemon>>(emptyList()) }

    fun suivant() {
        if (pokemons.isEmpty()) return
        val pokesFiltered = when (settings.difficulty) {
            Difficulty.EASY -> pokemons.take(649)
            else -> pokemons
        }
        if (pokesFiltered.size < 4) return

        val chosen = pokesFiltered.random()
        currentPokemonId = chosen.id

        val fauxCount = when {
            score >= 5000 -> when (settings.difficulty) {
                Difficulty.HARD -> 5
                Difficulty.MEDIUM -> 5
                else -> 4
            }
            score >= 2500 -> when (settings.difficulty) {
                Difficulty.HARD -> 5
                Difficulty.MEDIUM -> 4
                else -> 3
            }
            score >= 1000 -> when (settings.difficulty) {
                Difficulty.HARD -> 4
                Difficulty.MEDIUM -> 3
                else -> 2
            }
            else -> when (settings.difficulty) {
                Difficulty.HARD -> 4
                Difficulty.MEDIUM -> 2
                else -> 1
            }
        }
        val others = pokesFiltered
            .filter { it.id != chosen.id }
            .shuffled()
            .take(fauxCount)

        options = (others + chosen).shuffled()
    }

    LaunchedEffect(pokemons) {
        if (pokemons.isNotEmpty() && currentPokemonId == -1) suivant()
    }

    if (finDePartie) {
        EndOfGame(score, "Cris")
    } else {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .background(Primary)
        ) {
            val isLandscape = maxWidth > maxHeight
            Column(
                modifier = Modifier
                    .fillMaxSize(.95f)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Secondary),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = "Cris Quiz",
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
                    if (hintsCount > 0 && settings.difficulty != Difficulty.HARD && coeurs >= 1) {
                        Button(
                            onClick = {
                                hintsCount = 0
                                suivant()
                            },
                            modifier = Modifier
                                .height(25.dp)
                                .width(80.dp)
                                .align(Alignment.Center),
                            contentPadding = PaddingValues(0.dp),
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
                }
                Button(
                    onClick = {
                        currentPokemon?.let { playCry(context, it.id, settings.soundVolume) }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IdBgColor,
                        contentColor = Color.White
                    ),
                ) {
                    Text("Jouer le cri")
                }
                Spacer(Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxSize(.8f)
                        .background(BgColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLandscape) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            items(options) { pokemon ->
                                PokemonOptionItem(
                                    pokemon = pokemon,
                                    score = score
                                ) {
                                    if (pokemon.id == currentPokemon?.id) {
                                        score += 200
                                        hintsCount = 0
                                        suivant()
                                    } else {
                                        coeurs--
                                        hintsCount++
                                        if (coeurs <= 0) finDePartie = true
                                    }
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            items(options) { pokemon ->
                                PokemonOptionItem(
                                    pokemon = pokemon,
                                    score = score,
                                ) {
                                    if (pokemon.id == currentPokemon?.id) {
                                        score += 200
                                        hintsCount = 0
                                        suivant()
                                    } else {
                                        coeurs--
                                        hintsCount++
                                        if (coeurs <= 0) finDePartie = true
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
