package at.xa1.modulemate.commandmode

import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.command.Command
import at.xa1.modulemate.command.CommandList
import at.xa1.modulemate.ui.ListBox
import at.xa1.modulemate.ui.ScreenContext
import at.xa1.modulemate.ui.TextBox
import at.xa1.modulemate.ui.UiUserInput
import at.xa1.modulemate.ui.moveDown
import at.xa1.modulemate.ui.moveUp
import at.xa1.modulemate.ui.print
import at.xa1.modulemate.ui.reduce
import at.xa1.modulemate.ui.updateHeight
import at.xa1.modulemate.ui.updateItems

fun commandScreen(context: ScreenContext, state: CommandScreenState) = context.printScreen {
    print(state.searchBox, context.size.columns)

    print(state.listBox)

    resetCursor()
    print(CliColor.cursorDown(1) + CliColor.cursorRight(5 + state.searchBox.cursor))
}

data class CommandScreenState(
    val searchBox: TextBox,
    val listBox: ListBox<Command>
)

fun CommandScreenState.reduce(input: UiUserInput, commandList: CommandList, height: Int): CommandScreenState {
    return when (input) {
        UiUserInput.Arrow.Down -> copy(listBox = listBox.moveDown())
        UiUserInput.Arrow.Up -> copy(listBox = listBox.moveUp())
        else -> copy(searchBox = searchBox.reduce(input)).updateList(commandList)
    }.updateHeight(height)
}

internal fun CommandScreenState.updateList(commandList: CommandList): CommandScreenState =
    copy(
        listBox = listBox.updateItems(
            commandList.allCommands.filter { command ->
                command.shortcuts.any { shortcut -> shortcut.contains(searchBox.text) }
            }
        )
    )

internal fun CommandScreenState.updateHeight(height: Int): CommandScreenState =
    copy(listBox = listBox.updateHeight(height - 4))
