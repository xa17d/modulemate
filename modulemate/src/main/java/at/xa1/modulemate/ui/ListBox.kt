package at.xa1.modulemate.ui

import at.xa1.modulemate.cli.CliColor
import kotlin.math.min

data class ListBox<T>(
    val items: List<T>,
    val selectedIndex: Int = -1,
    val topIndex: Int = 0
)

fun <T> ListBox<T>.reduce(input: UiUserInput): ListBox<T> {
    return when (input) {
        UiUserInput.Arrow.Down -> copy(selectedIndex = (selectedIndex + 1).coerceAtMost(items.lastIndex))
        UiUserInput.Arrow.Up -> copy(selectedIndex = (selectedIndex - 1).coerceAtLeast(-1))
        else -> this
    }
}

fun <T> ScreenContext.print(listBox: ListBox<T>, height: Int) {
    val visibleRange = listBox.items.subList(listBox.topIndex, min(listBox.items.lastIndex, height))

    visibleRange.forEachIndexed { index, item ->
        if (index == listBox.selectedIndex) {
            print(CliColor.REVERSED)
            print("> " + item.toString())
            print(CliColor.RESET)
        } else {
            print("  " + item.toString())
        }
        print("\n")
    }
}
