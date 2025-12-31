package fr.uparis.jinzana.pcmprojet

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter

import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


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
fun Cris_Quiz(
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

            val isShadow =
                settings.difficulty == Difficulty.HARD &&
                        (0..99).random() < shadowChance

            if (isShadow) {
                ShadowSprite(pokemon.id, size = 100.dp)
            } else {
                PokemonSprite(pokemon.id, size = 100.dp)
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = onClick) {
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
            score >= 5000 -> if(settings.difficulty == Difficulty.HARD) 5 else if (settings.difficulty == Difficulty.MEDIUM) 5 else 4
            score >= 2500 -> if(settings.difficulty == Difficulty.HARD) 5 else if (settings.difficulty == Difficulty.MEDIUM) 4 else 3
            score >= 1000 -> if(settings.difficulty == Difficulty.HARD) 4 else if (settings.difficulty == Difficulty.MEDIUM) 3 else 2
            else ->  if(settings.difficulty == Difficulty.HARD) 4 else if (settings.difficulty == Difficulty.MEDIUM) 2 else 1
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
        End_of_game(score, "Cris")
    } else {
        BoxWithConstraints {
            val isLandscape = maxWidth > maxHeight

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = "Score : $score",
                    fontFamily = Pokemon_Police,
                    fontSize = if (isLandscape ) 20.sp else 15.sp,
                    modifier = Modifier.align(Alignment.CenterStart)
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
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }




            Button(onClick = {
                currentPokemon?.let { playCry(context, it.id, settings.soundVolume) }

            }) {
                Text("Jouer le cri")
            }

            Spacer(Modifier.height(24.dp))



            var namepoke: String

            if (isLandscape) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
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
                }
            }
            else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
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




            Spacer(Modifier.height(24.dp))


        }
    }
}}
