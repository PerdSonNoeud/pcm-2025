package fr.uparis.jinzana.pcmprojet

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.jinzana.pcmprojet.ui.theme.Secondary
import fr.uparis.jinzana.pcmprojet.ui.theme.BgColor
import fr.uparis.jinzana.pcmprojet.ui.theme.IdBgColor
import fr.uparis.jinzana.pcmprojet.ui.theme.NameBgColor
import fr.uparis.jinzana.pcmprojet.ui.theme.Selected
import kotlin.math.roundToInt


@Composable
fun PokedexQuiz(
    modifier: Modifier,
    viewModel: PokedexViewModel = viewModel()
) {
    // List of all the pokemons in the DAO
    val pokemons by viewModel.pokemons.collectAsState(initial = emptyList())
    var rdPokemon by remember(pokemons) {
        mutableStateOf(
            if (pokemons.isNotEmpty())
                pokemons[kotlin.random.Random.nextInt(pokemons.size)]
            else
                null
        )
    }
    val pokeList = remember { mutableStateListOf<Pokemon>() }

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
        // list of pokemons from DB
        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .background(BgColor)
                .fillMaxHeight()
        ) {
            if (rdPokemon != null) {
                stickyHeader {
                    val offsetY = remember { mutableFloatStateOf(0f) }
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(0, offsetY.floatValue.roundToInt()) }
                            .zIndex(1f)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        offsetY.floatValue = (offsetY.floatValue + dragAmount.y).coerceIn(-1000f, 1000f)
                                    },

                                    onDragEnd = {
                                        rdPokemon?.let { it ->
                                            pokeList.add(it)
                                            val isSorted = pokeList.zipWithNext { a, b -> a.id <= b.id }.all { it }
                                            println("pokeList sorted by id: $isSorted")
                                        }
                                        rdPokemon = if (pokemons.isNotEmpty())
                                            pokemons[kotlin.random.Random.nextInt(pokemons.size)]
                                        else
                                            null
                                        offsetY.floatValue = 0f
                                    }
                                )
                            }
                    ) {
                        rdPokemon?.let {
                            ShowPokemon(it, bgColor = Selected, hide = true)
                        }
                    }
                }
            }
            if (pokeList.isNotEmpty()) {
                items(pokeList) { p ->
                    ShowPokemon(p)
                }
            }
        }
    }
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
    return if (id < 10) {
        "000$id"
    } else if (id < 100) {
        "00$id"
    } else if (id < 1000) {
        "0$id"
    } else {
        "$id"
    }
}