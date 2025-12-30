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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.jinzana.pcmprojet.ui.theme.Secondary
import fr.uparis.jinzana.pcmprojet.ui.theme.BgColor
import fr.uparis.jinzana.pcmprojet.ui.theme.IdBgColor
import fr.uparis.jinzana.pcmprojet.ui.theme.NameBgColor
import fr.uparis.jinzana.pcmprojet.ui.theme.Selected


@Composable
fun PokedexQuiz(
    modifier: Modifier,
    viewModel: PokedexViewModel = viewModel()
) {
    // List of all the pokemons in the DAO
    val pokemons by viewModel.pokemons.collectAsState(initial = emptyList())
    val pokeList = remember { mutableStateListOf<Pokemon>() }
    // Initialize the pokeList only once (avoid repeated adds on recomposition)
    if (pokemons.isNotEmpty() && pokeList.isEmpty()) {
        pokeList.add(pokemons[kotlin.random.Random.nextInt(pokemons.size)])
        pokeList.add(pokemons[kotlin.random.Random.nextInt(pokemons.size)])
    }

    // --- added: tracked sorted state, initialize from current pokeList ---
    val isPokeListSorted = remember {
        mutableStateOf(pokeList.zipWithNext().all { (a, b) -> a.id <= b.id })
    }

    val listState = rememberLazyListState()
    val dragDropState = rememberDragDropListState(
        listState = listState,
        onMove = { from, to ->
            pokeList.add(to, pokeList.removeAt(from))
        }
    )

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

        // --- added: UI indicator for whether the list is sorted ---
        Text(
            text = "Sorted: ${if (isPokeListSorted.value) "Yes" else "No"}",
            modifier = Modifier
                .padding(start = 8.dp, bottom = 4.dp)
        )

        // list of pokemons from DB
        LazyColumn(
            // pass the list state so layoutInfo.visibleItemsInfo is correct
            state = listState,
            modifier = Modifier
                .padding(8.dp)
                .background(BgColor)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress (
                        // only attempt to start a drag when touching index 0
                        onDragStart = { dragDropState.onDragStart(it, 0) },
                        onDragEnd = {
                            // only finalize if a drag was actually active
                            if (dragDropState.draggedIndex != null) {
                                dragDropState.onDragEnd()
                                isPokeListSorted.value = pokeList.zipWithNext().all { (a, b) -> a.id <= b.id }
                                pokeList.add(0, newRandomPokemon(pokemons, pokeList))
                            }
                        },
                        onDragCancel = {
                            // only cancel if a drag was actually active
                            if (dragDropState.draggedIndex != null) {
                                dragDropState.onDragEnd()
                                isPokeListSorted.value = pokeList.zipWithNext().all { (a, b) -> a.id <= b.id }
                            }
                        },
                        onDrag = { _, dragAmount ->
                            // only forward drag deltas if a drag was started
                            if (dragDropState.draggedIndex != null) {
                                dragDropState.onDrag(dragAmount)
                            }
                        }
                    )
                }
        ) {
            if (pokeList.isNotEmpty()) {
                // use a stable key (item.id) and apply Selected to the dragged item
                itemsIndexed(items = pokeList, key = { _, item -> item.id }) { i, p ->
                    // mark item as dragging if its index matches the draggedIndex
                    val isDragging = dragDropState.draggedIndex == i
                    val isAnyDragging = dragDropState.draggedIndex != null

                    Box (
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(
                                // use getDraggedOffset() so the visual follows the draggable correctly
                                translationY = if (isDragging) dragDropState.getDraggedOffset() else 0f,
                                shadowElevation = if (isDragging) 8f else 0f
                            )
                    ) {
                        when {
                            // always show the dragged item with Selected (and not hidden)
                            isDragging -> ShowPokemon(p, Selected, true)
                            // show the first item with Selected only when nothing is dragging
                            i == 0 && !isAnyDragging -> ShowPokemon(p, Selected, true)
                            else -> ShowPokemon(p)
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
    // avoid out-of-bounds and handle empty filtered list
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