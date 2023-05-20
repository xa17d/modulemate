package at.xa1.modulemate.flashmode

import at.xa1.modulemate.command.Command
import at.xa1.modulemate.command.CommandList
import at.xa1.modulemate.liveui.LiveUiMode
import at.xa1.modulemate.ui.ListBox
import at.xa1.modulemate.ui.ListBoxItemRenderer
import at.xa1.modulemate.ui.TextBox
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput

class FlashMode(
    private val ui: Ui,
    private val commandList: CommandList
) : LiveUiMode {
    private var state = FlashScreenState(
        searchBox = TextBox(
            hint = "Flash Mode",
            emoji = "⚡️"
        ),
        listBox = ListBox(
            items = commandList.allCommands.filter { command ->
                command.shortcuts.any { shortcut -> shortcut.length == 1 }
            },
            height = 0,
            renderer = object : ListBoxItemRenderer<Command> {
                override fun render(item: Command, isSelected: Boolean): String {
                    return item.shortcuts.filter { it.length == 1 }.toString() + ": " + item.name
                }
            }
        )
    )

    override fun print(input: UiUserInput?) {
        val context = ui.createScreenContext()

        if (input != null) {
            state = state.reduce(input, context.size.rows)
        }
        state = state.updateHeight(context.size.rows)

        context.printScreen {
            flashScreen(this, state)
        }
    }
}
