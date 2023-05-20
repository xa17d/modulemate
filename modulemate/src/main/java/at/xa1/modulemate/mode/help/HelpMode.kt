package at.xa1.modulemate.mode.help

import at.xa1.modulemate.Modulemate
import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.mode.LiveUiMode
import at.xa1.modulemate.ui.ListBox
import at.xa1.modulemate.ui.ListBoxItemRenderer
import at.xa1.modulemate.ui.TextBox
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput

class HelpMode(
    private val ui: Ui
) : LiveUiMode {
    private var state = HelpScreenState(
        searchBox = TextBox(
            hint = "Help Mode",
            emoji = "ℹ\uFE0F"
        ),
        listBox = ListBox(
            items = listOf(
                moduleMate(),
                "",
                "Use ${key("TAB")} and ${key("⇧")} + ${key("TAB")} to switch forward and backwards between modes.",
                "",
                header("🔍 Search Mode"),
                "Shows all available modules. Type to filter.",
                "Use ${key("▲")} and ${key("▼")} to navigate in the list.",
                "Press ${key("RETURN ⏎")} to activate or deactivate a module.",
                "${key("RETURN ⏎")} when selection is in search box will activate or deactivate all filtered modules.",
                "",
                header("📦 Module Mode"),
                "Shows active and recent modules.",
                "Use ${key("▲")} and ${key("▼")} to navigate in the list.",
                "Press ${key("RETURN ⏎")} to activate or deactivate a module.",
                "Use ${key("  ⌫")} to deactivate a module and remove it from recent.",
                "",
                header("🕹 Command Mode"),
                "Shows all available commands. Type to filter.",
                "Use ${key("▲")} and ${key("▼")} to navigate in the list.",
                "Press ${key("RETURN ⏎")} to execute a command.",
                "",
                header("⚡️ Flash Mode"),
                "Shows all commands with a one-character-shortcut.",
                "Type one character, and the corresponding command is immediately executed"
            ),
            height = 0,
            renderer = object : ListBoxItemRenderer<String> {
                override fun render(item: String, isSelected: Boolean): String {
                    return " " + CliColor.RESET + " " + item
                }
            }
        )
    )

    private fun moduleMate() =
        "🧰 ${CliColor.BOLD}${CliColor.RED}modulemate${CliColor.RESET} " +
            "${CliColor.BRIGHT_WHITE}v${Modulemate.VERSION}${CliColor.RESET}"

    private fun key(value: String) = CliColor.BACKGROUND_WHITE + CliColor.BLACK + " " + value + " " + CliColor.RESET
    private fun header(text: String) = CliColor.BOLD + CliColor.UNDERLINE + text + CliColor.RESET

    override fun print(input: UiUserInput?) {
        val context = ui.createScreenContext()

        if (input != null) {
            state = state.reduce(input, context.size.rows)
        }
        state = state.updateHeight(context.size.rows)

        context.printScreen {
            helpScreen(this, state)
        }
    }
}
