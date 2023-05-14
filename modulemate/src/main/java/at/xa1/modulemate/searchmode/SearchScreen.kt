package at.xa1.modulemate.searchmode

import at.xa1.modulemate.ui.ScreenContext
import at.xa1.modulemate.ui.TextBox
import at.xa1.modulemate.ui.UiUserInput
import at.xa1.modulemate.ui.print
import at.xa1.modulemate.ui.reduce

fun searchScreen(context: ScreenContext, state: SearchScreenState) = context.printScreen {
    print(state.searchBox, context.size.columns)

    setCursor(5 + state.searchBox.cursor, 1)
}

data class SearchScreenState(val searchBox: TextBox)

fun SearchScreenState.reduce(input: UiUserInput): SearchScreenState {
    return when (input) {
        UiUserInput.Arrow.Down -> TODO()
        UiUserInput.Arrow.Up -> TODO()
        UiUserInput.Escape -> TODO()
        UiUserInput.Return -> TODO()
        else -> {
            copy(searchBox = searchBox.reduce(input))
        }
    }
}
