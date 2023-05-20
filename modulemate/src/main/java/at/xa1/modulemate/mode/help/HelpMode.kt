package at.xa1.modulemate.mode.help

import at.xa1.modulemate.Modulemate
import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.mode.LiveUiMode
import at.xa1.modulemate.mode.SearchListScreen
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput

internal class HelpMode(
    private val ui: Ui
) : LiveUiMode {
    private val screen = SearchListScreen(
        emoji = "ℹ\uFE0F",
        hint = "Help Mode",
        listProvider = {
            listOf(
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
            )
        },
        listItemRenderer = { item, _ -> " " + CliColor.RESET + " " + item }
    )

    override fun print(input: UiUserInput?) {
        screen.print(ui)

        while (true) {
            when (val input = ui.readUserInput()) {
                UiUserInput.Tab, UiUserInput.Shift.Tab -> return
                UiUserInput.Arrow.Up,
                UiUserInput.Arrow.Down
                -> {
                    screen.input(input)
                    screen.print(ui)
                }

                else -> return
            }
        }
    }

    private fun moduleMate() =
        "🧰 ${CliColor.BOLD}${CliColor.RED}modulemate${CliColor.RESET} " +
            "${CliColor.BRIGHT_WHITE}v${Modulemate.VERSION}${CliColor.RESET}"

    private fun key(value: String) = CliColor.BACKGROUND_WHITE + CliColor.BLACK + " " + value + " " + CliColor.RESET
    private fun header(text: String) = CliColor.BOLD + CliColor.UNDERLINE + text + CliColor.RESET
}
