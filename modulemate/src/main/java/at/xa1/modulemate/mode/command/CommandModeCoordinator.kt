package at.xa1.modulemate.mode.command

import at.xa1.modulemate.UserCommandRunner
import at.xa1.modulemate.command.Command
import at.xa1.modulemate.command.CommandList
import at.xa1.modulemate.mode.ModeCoordinator
import at.xa1.modulemate.mode.SearchListScreen
import at.xa1.modulemate.ui.ListItemRenderer
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput

internal class CommandModeCoordinator(
    private val ui: Ui,
    private val commandList: CommandList,
    private val commandRunner: UserCommandRunner
) : ModeCoordinator {
    private val screen = SearchListScreen(
        emoji = "\uD83D\uDD79", // = 🕹️but without the  U+FE0F (VARIATION SELECTOR-16)
        hint = "Command Mode",
        listProvider = { filter ->
            commandList.allCommands.filter { command ->
                command.shortcuts.any { shortcut ->
                    shortcut.contains(filter)
                }
            }
        },
        listItemRenderer = object : ListItemRenderer<Command> {
            override fun render(item: Command, isSelected: Boolean): String {
                return item.shortcuts.toString() + " " + item.name
            }
        }
    )

    override fun run(): UiUserInput {
        screen.print(ui)

        while (true) {
            when (val input = ui.readUserInput()) {
                UiUserInput.Tab, UiUserInput.Shift.Tab -> return input
                UiUserInput.Return -> executeCommand()
                else -> {
                    screen.input(input)
                    screen.print(ui)
                }
            }
        }
    }

    private fun executeCommand() {
        val command = screen.selectedItem ?: screen.state.listBox.items.firstOrNull()
        if (command != null) {
            screen.moveCursorAfterTextBox(ui)

            commandRunner.run(command)

            screen.printOnlyTextBox(ui, command.name)
        }
    }
}
