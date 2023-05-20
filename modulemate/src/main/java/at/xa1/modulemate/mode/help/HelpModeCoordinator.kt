package at.xa1.modulemate.mode.help

import at.xa1.modulemate.Modulemate
import at.xa1.modulemate.cli.CliFormat
import at.xa1.modulemate.mode.ModeCoordinator
import at.xa1.modulemate.mode.SearchListScreen
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput

internal class HelpModeCoordinator(
    private val ui: Ui
) : ModeCoordinator {
    private val screen = SearchListScreen(
        emoji = "ℹ\uFE0F",
        hint = "Help Mode",
        listProvider = {
            listOf(
                moduleMate(),
                "",
                "Use ${formatKey("TAB")} and ${formatKey("⇧")} + ${formatKey("TAB")} " +
                    "to switch forward and backwards between modes.",
                "",
                header("🔍 Search Mode"),
                "Shows all available modules. Type to filter.",
                "Use ${formatKey("▲")} and ${formatKey("▼")} to navigate in the list.",
                "Press ${formatKey("RETURN ⏎")} to activate or deactivate a module.",
                "${formatKey("RETURN ⏎")} when selection is in search box will activate or deactivate " +
                    "all filtered modules.",
                "",
                header("📦 Module Mode"),
                "Shows active and recent modules.",
                "Use ${formatKey("▲")} and ${formatKey("▼")} to navigate in the list.",
                "Press ${formatKey("RETURN ⏎")} to activate or deactivate a module.",
                "Use ${formatKey("  ⌫")} to deactivate a module and remove it from recent.",
                "",
                header("🕹 Command Mode"),
                "Shows all available commands. Type to filter.",
                "Use ${formatKey("▲")} and ${formatKey("▼")} to navigate in the list.",
                "Press ${formatKey("RETURN ⏎")} to execute a command.",
                "",
                header("⚡️ Flash Mode"),
                "Shows all commands with a one-character-shortcut.",
                "Type one character, and the corresponding command is immediately executed"
            )
        },
        listItemRenderer = { item, _ -> " " + CliFormat.RESET + " " + item }
    )

    override fun run(): UiUserInput {
        screen.print(ui)

        while (true) {
            when (val input = ui.readUserInput()) {
                UiUserInput.Tab, UiUserInput.Shift.Tab, UiUserInput.Escape -> return input
                UiUserInput.Arrow.Up,
                UiUserInput.Arrow.Down -> {
                    screen.input(input)
                    screen.print(ui)
                }

                else -> return input
            }
        }
    }

    private fun moduleMate() =
        "🧰 ${CliFormat.BOLD}${CliFormat.RED}modulemate${CliFormat.RESET} " +
            "${CliFormat.BRIGHT_WHITE}v${Modulemate.VERSION}${CliFormat.RESET}"
    private fun header(text: String) = CliFormat.BOLD + CliFormat.UNDERLINE + text + CliFormat.RESET
}

fun formatKey(value: String) = CliFormat.BACKGROUND_WHITE + CliFormat.BLACK + " " + value + " " + CliFormat.RESET
