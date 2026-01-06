package fr.uparis.jinzana.pcmprojet.utils

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset


@Composable
fun rememberDragDropListState(
    listState: LazyListState,
    onMove: (Int, Int) -> Unit
): DragDropListState {
    return remember {
        DragDropListState(listState, onMove)
    }
}


class DragDropListState(
    private val listState: LazyListState,
    private val onMove: (Int, Int) -> Unit
) {
    var draggedIndex by mutableStateOf<Int?>(null)
        private set

    private var initialItem by mutableStateOf<LazyListItemInfo?>(null)

    var draggedDistance by mutableFloatStateOf(0f)
        private set

    // accept an optional allowedStartIndex: if provided, only start drag when touched item.index == allowedStartIndex
    fun onDragStart(offset: Offset, allowedStartIndex: Int? = null) {
        listState.layoutInfo.visibleItemsInfo
            .firstOrNull{
                offset.y.toInt() in it.offset..(it.offset + it.size)
            }?.let { itemInfo ->
                if (allowedStartIndex == null || itemInfo.index == allowedStartIndex) {
                    draggedIndex = itemInfo.index
                    initialItem = itemInfo
                }
            }
    }

    fun onDrag(delta: Offset) {
        draggedDistance += delta.y

        val fromIndex = draggedIndex ?: return
        val fromItem = listState.findItemInfo(fromIndex) ?: return
        val initialOffset = initialItem?.offset ?: return

        val currentTop = initialOffset + draggedDistance
        val currentBottom = currentTop + fromItem.size

        listState.layoutInfo.visibleItemsInfo
            .filter {
                it.index != fromIndex && it.overlaps(currentTop.toInt(), currentBottom.toInt())
            }
            .firstOrNull { candidate ->
                if (draggedDistance > 0) {
                    currentBottom > candidate.offset + candidate.size
                } else {
                    currentTop < candidate.offset
                }
            }?.let { targetItem ->
                onMove(fromIndex, targetItem.index)
                draggedIndex = targetItem.index
                initialItem = targetItem
                draggedDistance = 0f
            }
    }

    fun onDragEnd() {
        draggedIndex = null
        draggedDistance = 0f
        initialItem = null
    }

    fun getDraggedOffset(): Float {
        val original = initialItem?.offset ?: return 0f
        val current = listState.findItemInfo(draggedIndex ?: return 0f)?.offset ?: return 0f
        return original + draggedDistance - current
    }

}

private fun LazyListState.findItemInfo(index: Int): LazyListItemInfo? {
    return layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
}

private fun LazyListItemInfo.overlaps(top: Int, bottom: Int): Boolean {
    return offset < bottom && (offset + size) > top
}
