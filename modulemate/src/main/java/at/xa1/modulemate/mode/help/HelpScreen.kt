package at.xa1.modulemate.mode.help

import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.ui.ListBox
import at.xa1.modulemate.ui.ScreenContext
import at.xa1.modulemate.ui.TextBox
import at.xa1.modulemate.ui.UiUserInput
import at.xa1.modulemate.ui.moveDown
import at.xa1.modulemate.ui.moveUp
import at.xa1.modulemate.ui.print
import at.xa1.modulemate.ui.reduce
import at.xa1.modulemate.ui.updateHeight

fun helpScreen(context: ScreenContext, state: HelpScreenState) = context.printScreen {
    print(state.searchBox, context.size.columns)

    print(state.listBox)

    resetCursor()
    print(CliColor.cursorDown(1) + CliColor.cursorRight(5 + state.searchBox.cursor))
}

data class HelpScreenState(
    val searchBox: TextBox,
    val listBox: ListBox<String>
)

fun HelpScreenState.reduce(input: UiUserInput, height: Int): HelpScreenState {
    return when (input) {
        UiUserInput.Arrow.Down -> copy(listBox = listBox.moveDown())
        UiUserInput.Arrow.Up -> copy(listBox = listBox.moveUp())

        UiUserInput.Return -> {
            println("TODO execute")
            this
        }
        else -> {
            copy(searchBox = searchBox.reduce(input))
        }
    }.updateHeight(height)
}

internal fun HelpScreenState.updateHeight(height: Int): HelpScreenState =
    copy(listBox = listBox.updateHeight(height - 4))
