package fr.uparis.jinzana.pcmprojet.quiz

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.jinzana.pcmprojet.ui.theme.Secondary
import fr.uparis.jinzana.pcmprojet.ui.theme.BgColor
import fr.uparis.jinzana.pcmprojet.ui.theme.IdBgColor
import fr.uparis.jinzana.pcmprojet.ui.theme.NameBgColor
import fr.uparis.jinzana.pcmprojet.ui.theme.Selected
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.sp
import fr.uparis.jinzana.pcmprojet.utils.settings.Difficulty
import fr.uparis.jinzana.pcmprojet.End_of_game
import fr.uparis.jinzana.pcmprojet.Pokemon_Police
import fr.uparis.jinzana.pcmprojet.R
import fr.uparis.jinzana.pcmprojet.utils.settings.SettingsViewModel
import fr.uparis.jinzana.pcmprojet.utils.settings.SettingsViewModelFactory
import fr.uparis.jinzana.pcmprojet.utils.music.SoundViewModel
import fr.uparis.jinzana.pcmprojet.utils.music.SoundViewModelFactory
import fr.uparis.jinzana.pcmprojet.database.PokeViewModel
import fr.uparis.jinzana.pcmprojet.database.PokeViewModelFactory
import fr.uparis.jinzana.pcmprojet.database.Pokemon
import fr.uparis.jinzana.pcmprojet.utils.rememberDragDropListState
import fr.uparis.jinzana.pcmprojet.utils.music.PageMusic
import kotlin.random.Random

@Composable
fun StatQuiz(
    modifier: Modifier
) {
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current)
    )
    val soundVM: SoundViewModel = viewModel(
        factory = SoundViewModelFactory(LocalContext.current)
    )

    val settings by settingsViewModel.settings.collectAsState()
    PageMusic(R.raw.shadow, settings.musicVolume)

    val pokeVM: PokeViewModel = viewModel(
        factory = PokeViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val pokemons by pokeVM.allPokemons.collectAsState(initial = emptyList())
    var savedPokeIds by rememberSaveable { mutableStateOf(listOf<Int>()) }

    val pokeList = remember { mutableStateListOf<Pokemon>() }

    val stats = listOf("Health", "Attack", "Defense", "Sp. Atk", "Sp. Def", "Speed")
    val rdStats = remember {
        stats[Random.nextInt(stats.size)]
    }

    var rd by remember(pokemons) {
        mutableStateOf(
            if (pokemons.isNotEmpty())
                pokemons[Random.nextInt(pokemons.size)]
            else
                null
        )
    }
    if (pokemons.isNotEmpty() && pokeList.isEmpty()) {
        if (savedPokeIds.isNotEmpty()) {
            // reconstruct the pokeList using saved ids (ignore ids not found)
            savedPokeIds.forEach { id ->
                pokemons.find { it.id == id }?.let { pokeList.add(it) }
            }
            // if reconstruction failed (e.g. DB changed) ensure at least one random item
            if (pokeList.isEmpty()) {
                rd = newRandomPokemon(pokemons, pokeList)
                pokeList.add(0, rd!!)
                pokeList.add(pokemons[Random.nextInt(pokemons.size)])
            }
        } else {
            rd = newRandomPokemon(pokemons, pokeList)
            pokeList.add(0, rd!!)
            pokeList.add(pokemons[Random.nextInt(pokemons.size)])
        }
    }

    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val dragDropState = rememberDragDropListState(
        listState = listState,
        onMove = { from, to ->
            pokeList.add(to, pokeList.removeAt(from))
            // persist new ordering immediately
            savedPokeIds = pokeList.map { it.id }
        }
    )

    var score by rememberSaveable { mutableIntStateOf(0) }
    val initialCoeurs = when (settings.difficulty) {
        Difficulty.EASY -> 5
        Difficulty.MEDIUM -> 3
        Difficulty.HARD -> 1
    }
    var coeurs by rememberSaveable { mutableIntStateOf(initialCoeurs) }
    var finDePartie by rememberSaveable { mutableStateOf(false) }

    if (finDePartie) {
        End_of_game(score, "Pokedex")
    } else {
        Column(
            modifier = modifier
                .background(Secondary)
        ) {
            Text(
                text = "Stat Quiz - $rdStats",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .background(IdBgColor)
                    .fillMaxWidth()
            )

            // replaced: show score on left and lives on right (like Shadow_Quiz)
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Score: $score",
                    fontFamily = Pokemon_Police,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp)
                )
                Text(
                    text = "\uD83D\uDDA4".repeat(initialCoeurs - coeurs) + "❤️".repeat(coeurs),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp)
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(8.dp)
                    .background(BgColor)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress (
                            onDragStart = { dragDropState.onDragStart(it, 0) },
                            onDragEnd = {
                                if (dragDropState.draggedIndex != null) {
                                    dragDropState.onDragEnd()
                                    if (pokeList.zipWithNext().all { (a, b) -> a.id <= b.id }) {
                                        score += 200
                                        soundVM.play("click", settings.soundVolume)
                                    } else {
                                        coeurs -= 1
                                        if (coeurs <= 0) {
                                            finDePartie = true
                                        }
                                        pokeList.remove(rd)
                                    }
                                    if (!finDePartie) {
                                        rd = newRandomPokemon(pokemons, pokeList)
                                        pokeList.add(0, rd!!)
                                    }
                                    savedPokeIds = pokeList.map { it.id }
                                }
                            },
                            onDrag = { _, dragAmount ->
                                if (dragDropState.draggedIndex != null) {
                                    dragDropState.onDrag(dragAmount)
                                }
                            }
                        )
                    }
            ) {
                if (pokeList.isNotEmpty()) {
                    itemsIndexed(items = pokeList, key = { _, item -> item.id }) { i, p ->
                        val isDragging = dragDropState.draggedIndex == i
                        val isAnyDragging = dragDropState.draggedIndex != null

                        Box (
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer(
                                    translationY = if (isDragging) dragDropState.getDraggedOffset() else 0f,
                                    shadowElevation = if (isDragging) 8f else 0f
                                )
                        ) {
                            when {
                                isDragging -> ShowPokemonStat(p, rdStats, Selected, true)
                                i == 0 && !isAnyDragging -> ShowPokemonStat(p, rdStats, Selected, true)
                                else -> ShowPokemonStat(p, rdStats)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowPokemonStat(
    p: Pokemon,
    value: String,
    bgColor: Color = NameBgColor,
    hide: Boolean = false
) {
    val stats = mapOf(
        "Health" to p.health,
        "Attack" to p.attack,
        "Defense" to p.defense,
        "Sp. Atk" to p.specialAttack,
        "Sp. Def" to p.specialDefense,
        "Speed" to p.speed
    )

    Row {
        if (hide) {
            Text(
                text = "   000   ",
                modifier = Modifier
                    .padding(4.dp)
                    .background(IdBgColor)
            )
        } else {
            Text(
                text = "   ${statToString(stats[value]!!)}   ",
                modifier = Modifier
                    .padding(4.dp)
                    .background(IdBgColor)
            )
        }
        Text(
            text = "  ${p.name}",
            color = Secondary,
            modifier = Modifier
                .padding(4.dp)
                .background(bgColor)
                .fillMaxWidth()
        )
    }
}

fun statToString(value: Int): String {
    return when {
        value < 10 -> "00$value"
        value < 100 -> "0$value"
        value < 1000 -> "$value"
        else -> "$value"
    }
}