package at.xa1.modulemate.commandmode

import at.xa1.modulemate.UserCommandRunner
import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.command.Command
import at.xa1.modulemate.command.CommandList
import at.xa1.modulemate.liveui.LiveUiMode
import at.xa1.modulemate.ui.ListBox
import at.xa1.modulemate.ui.ListBoxItemRenderer
import at.xa1.modulemate.ui.TextBox
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput
import at.xa1.modulemate.ui.print
import at.xa1.modulemate.ui.selectedItemOrNull

internal class CommandMode(
    private val ui: Ui,
    private val commandList: CommandList,
    private val commandRunner: UserCommandRunner
) : LiveUiMode {
    private var state = CommandScreenState(
        searchBox = TextBox(
            hint = "Command Mode",
            emoji = "\uD83D\uDD79" // = 🕹️but without the  U+FE0F (VARIATION SELECTOR-16)
        ),
        listBox = ListBox(
            items = commandList.allCommands,
            height = 0,
            renderer = object : ListBoxItemRenderer<Command> {
                override fun render(item: Command, isSelected: Boolean): String {
                    return item.shortcuts.toString() + " " + item.name
                }
            }
        )
    )

    override fun print(input: UiUserInput?) {
        val context = ui.createScreenContext()

        if (input == UiUserInput.Return) {
            val command = state.listBox.selectedItemOrNull()
            if (command != null) {
                context.resetCursor()
                context.print(CliColor.cursorDown(3) + CliColor.CLEAR_UNTIL_END_OF_SCREEN)
                context.flush()

                commandRunner.run(command)

                context.print("\n")
                context.print(state.searchBox, context.size.columns)
                context.flush()
                print(CliColor.cursorUp(2) + CliColor.cursorRight(5 + state.searchBox.cursor))
                context.flush()
            }
        } else {
            if (input != null) {
                state = state.reduce(input, commandList, context.size.rows)
            }
            state = state.updateList(commandList).updateHeight(context.size.rows)

            context.printScreen {
                commandScreen(this, state)
            }
        }
    }
}
