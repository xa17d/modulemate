package at.xa1.modulemate.mode.flash

import at.xa1.modulemate.UserCommandRunner
import at.xa1.modulemate.command.Command
import at.xa1.modulemate.command.CommandList
import at.xa1.modulemate.mode.ModeCoordinator
import at.xa1.modulemate.mode.SearchListScreen
import at.xa1.modulemate.mode.help.formatKey
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput

internal class FlashModeCoordinator(
    private val ui: Ui,
    private val commandList: CommandList,
    private val commandRunner: UserCommandRunner,
) : ModeCoordinator {
    private val screen = SearchListScreen(
        emoji = "⚡️",
        hint = "Flash Mode: Execute commands with one key stroke",
        listProvider = {
            commandList.allCommands.filter { command -> command.oneCharShortCuts.isNotEmpty() }
        },
        listItemRenderer = { item, isSelected ->
            if (isSelected) {
                item.oneCharShortCuts.joinToString { " $it " } + " " + item.name
            } else {
                item.oneCharShortCuts.joinToString { formatKey(it) } + " " + item.name
            }
        }
    )

    private var lastCommand: Command? = null
    override fun run(): UiUserInput {
        screen.print(ui)

        while (true) {
            when (val input = ui.readUserInput()) {
                UiUserInput.Tab, UiUserInput.Shift.Tab, UiUserInput.Escape -> return input
                UiUserInput.Return -> {
                    val command = screen.selectedItem ?: lastCommand
                    if (command != null) {
                        executeCommand(command)
                    }
                }

                UiUserInput.Arrow.Up -> {
                    if (screen.selectedItem == null && lastCommand != null) {
                        executeCommand(lastCommand!!)
                    } else {
                        screenInput(input)
                    }
                }

                is UiUserInput.Char -> {
                    val command = commandList.getOrNull(input.char.toString())

                    if (command != null) {
                        executeCommand(command)
                    } else {
                        screen.print(ui)
                    }
                }

                else -> screenInput(input)
            }
        }
    }

    private fun screenInput(input: UiUserInput) {
        screen.input(input)
        screen.print(ui)
    }

    private fun executeCommand(command: Command) {
        screen.moveCursorAfterTextBox(ui)

        commandRunner.run(command)

        screen.printOnlyTextBox(ui, command.name)

        lastCommand = command
    }

    private val Command.oneCharShortCuts: List<String>
        get() = shortcuts.filter { shortcut -> shortcut.length == 1 }
}
