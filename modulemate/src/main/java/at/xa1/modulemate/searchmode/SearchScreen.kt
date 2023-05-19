package at.xa1.modulemate.searchmode

import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.ui.ListBox
import at.xa1.modulemate.ui.ScreenContext
import at.xa1.modulemate.ui.TextBox
import at.xa1.modulemate.ui.UiUserInput
import at.xa1.modulemate.ui.print
import at.xa1.modulemate.ui.reduce

fun searchScreen(context: ScreenContext, state: SearchScreenState) = context.printScreen {
    print(state.searchBox, context.size.columns)

    print(state.listBox, size.rows - 4)

    resetCursor()
    print(CliColor.cursorDown(1) + CliColor.cursorRight(5 + state.searchBox.cursor))
}

data class SearchScreenState(
    val searchBox: TextBox,
    val listBox: ListBox<String>,
)

fun SearchScreenState.reduce(input: UiUserInput): SearchScreenState {
    return when (input) {
        UiUserInput.Arrow.Down,
        UiUserInput.Arrow.Up,
        -> copy(listBox = listBox.reduce(input))

        UiUserInput.Escape -> TODO()
        UiUserInput.Return -> TODO()
        else -> {
            copy(searchBox = searchBox.reduce(input))
        }
    }
}
