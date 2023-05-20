package at.xa1.modulemate.mode.flash

import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.command.Command
import at.xa1.modulemate.ui.ListBox
import at.xa1.modulemate.ui.ScreenContext
import at.xa1.modulemate.ui.TextBox
import at.xa1.modulemate.ui.UiUserInput
import at.xa1.modulemate.ui.moveDown
import at.xa1.modulemate.ui.moveUp
import at.xa1.modulemate.ui.print
import at.xa1.modulemate.ui.updateHeight

fun flashScreen(context: ScreenContext, state: FlashScreenState) = context.printScreen {
    print(state.searchBox, context.size.columns)

    print(state.listBox)

    resetCursor()
    print(CliColor.cursorDown(1) + CliColor.cursorRight(5 + state.searchBox.cursor))
}

data class FlashScreenState(
    val searchBox: TextBox,
    val listBox: ListBox<Command>
)

fun FlashScreenState.reduce(input: UiUserInput, height: Int): FlashScreenState {
    return when (input) {
        UiUserInput.Arrow.Down -> copy(listBox = listBox.moveDown())
        UiUserInput.Arrow.Up -> copy(listBox = listBox.moveUp())
        else -> this
    }.updateHeight(height)
}
internal fun FlashScreenState.updateHeight(height: Int): FlashScreenState =
    copy(listBox = listBox.updateHeight(height - 4))
