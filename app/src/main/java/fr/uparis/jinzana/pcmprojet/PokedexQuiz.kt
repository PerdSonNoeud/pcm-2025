package fr.uparis.jinzana.pcmprojet

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

@Composable
fun PokedexQuiz(
    modifier: Modifier,
    viewModel: PokedexViewModel = viewModel()
) {
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current)
    )
    val soundVM: SoundViewModel = viewModel(
        factory = SoundViewModelFactory(LocalContext.current)
    )

    val settings by settingsViewModel.settings.collectAsState()
    PageMusic(R.raw.shadow, settings.musicVolume)

    val pokemons by viewModel.pokemons.collectAsState(initial = emptyList())
    var savedPokeIds by rememberSaveable { mutableStateOf(listOf<Int>()) }

    val pokeList = remember { mutableStateListOf<Pokemon>() }
    var rd by remember(pokemons) {
        mutableStateOf(
            if (pokemons.isNotEmpty())
                pokemons[kotlin.random.Random.nextInt(pokemons.size)]
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
                pokeList.add(pokemons[kotlin.random.Random.nextInt(pokemons.size)])
            }
        } else {
            rd = newRandomPokemon(pokemons, pokeList)
            pokeList.add(0, rd!!)
            pokeList.add(pokemons[kotlin.random.Random.nextInt(pokemons.size)])
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
                text = "Pokedex Quiz",
                textAlign = TextAlign.Center,
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
                                isDragging -> ShowPokemon(p, Selected, true)
                                i == 0 && !isAnyDragging -> ShowPokemon(p, Selected, true)
                                else -> ShowPokemon(p)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun newRandomPokemon(
    pokemons: List<Pokemon>,
    filter: List<Pokemon>
): Pokemon {
    val filteredPokemons = pokemons.filter { p -> !filter.contains(p) }
    if (filteredPokemons.isEmpty()) return pokemons.first()
    return filteredPokemons[kotlin.random.Random.nextInt(filteredPokemons.size)]
}

@Composable
fun ShowPokemon(
    p: Pokemon,
    bgColor: Color = NameBgColor,
    hide: Boolean = false
) {
    Row {
        if (hide) {
            Text(
                text = "  #0000  ",
                modifier = Modifier
                    .padding(4.dp)
                    .background(IdBgColor)
            )
        } else {
            Text(
                text = "  #${id(p.id)}  ",
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

fun id(id: Int): String {
    return when {
        id < 10 -> "000$id"
        id < 100 -> "00$id"
        id < 1000 -> "0$id"
        else -> "$id"
    }
}