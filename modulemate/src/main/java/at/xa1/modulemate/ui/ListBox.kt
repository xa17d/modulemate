package at.xa1.modulemate.ui

import at.xa1.modulemate.cli.CliFormat
import kotlin.math.max

data class ListBox<T>(
    val items: List<T>,
    val selectedIndex: Int = -1,
    val topIndex: Int = 0,
    val height: Int,
    val itemRenderer: ListItemRenderer<T>,
)

fun interface ListItemRenderer<T> {
    fun render(
        item: T,
        isSelected: Boolean,
    ): String
}

fun <T> ListBox<T>.selectedItemOrNull(): T? = items.getOrNull(selectedIndex)

fun <T> ListBox<T>.moveDown(): ListBox<T> = copy(selectedIndex = (selectedIndex + 1).coerceAtMost(items.lastIndex))

fun <T> ListBox<T>.moveUp(): ListBox<T> = copy(selectedIndex = (selectedIndex - 1).coerceAtLeast(-1))

fun <T> ListBox<T>.updateItems(newItems: List<T>): ListBox<T> {
    val selectedItem = selectedItemOrNull()
    val newSelectedIndex = newItems.indexOf(selectedItem)

    return copy(
        items = newItems,
        selectedIndex = newSelectedIndex,
    ).updateHeight(height)
}

fun <T> ListBox<T>.updateHeight(newHeight: Int): ListBox<T> {
    val newTopIndex =
        when {
            selectedIndex == -1 -> 0
            selectedIndex < topIndex + 2 -> (selectedIndex - 1).coerceAtLeast(0)
            selectedIndex > topIndex + newHeight - 2 -> {
                selectedIndex - newHeight + 2
            }

            else -> topIndex
        }.coerceAtMost(max(items.size - newHeight, 0))

    return copy(height = newHeight, topIndex = newTopIndex)
}

fun <T> ScreenContext.print(listBox: ListBox<T>) {
    val visibleRange =
        listBox.items.subList(
            listBox.topIndex,
            listBox.items.size.coerceIn(0, listBox.height + listBox.topIndex),
        )

    visibleRange.forEachIndexed { visibleIndex, item ->
        val index = visibleIndex + listBox.topIndex
        val isSelected = (index == listBox.selectedIndex)
        val renderedItem = listBox.itemRenderer.render(item, isSelected)
        if (isSelected) {
            print(CliFormat.REVERSED)
            print(renderedItem)
            print(CliFormat.RESET)
        } else {
            print(renderedItem)
        }

        if (index != listBox.items.lastIndex) {
            print("\n")
        }
    }
}
