package at.xa1.modulemate.mode

import at.xa1.modulemate.UserCommandRunner
import at.xa1.modulemate.command.CommandList
import at.xa1.modulemate.mode.command.CommandMode
import at.xa1.modulemate.mode.flash.FlashMode
import at.xa1.modulemate.mode.help.HelpMode
import at.xa1.modulemate.mode.modules.ModulesMode
import at.xa1.modulemate.mode.search.SearchMode
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput

internal class LiveUi(
    private val ui: Ui,
    modules: Modules,
    commandList: CommandList,
    commandRunner: UserCommandRunner
) {
    private val helpMode = HelpMode(ui)
    private val searchMode = SearchMode(ui, modules)
    private val modulesMode = ModulesMode(ui, modules)
    private val commandMode = CommandMode(ui, commandList, commandRunner)
    private val flashMode = FlashMode(ui, commandList, commandRunner)
    private val modes = listOf(
        helpMode,
        searchMode,
        modulesMode,
        commandMode,
        flashMode
    )

    private var currentMode: LiveUiMode = helpMode

    private fun moveMode(delta: Int) {
        val currentModeIndex = modes.indexOf(currentMode)
        val nextIndex = (currentModeIndex + delta).mod(modes.size)
        currentMode = modes[nextIndex]
    }

    fun run() {
        currentMode.print(null)
        while (true) {
            val input = ui.readUserInput()

            val inputForMode: UiUserInput? = when (input) {
                UiUserInput.Tab -> {
                    moveMode(1)
                    null
                }

                UiUserInput.Shift.Tab -> {
                    moveMode(-1)
                    null
                }

                is UiUserInput.Char -> {
                    if (currentMode == modulesMode) {
                        currentMode = searchMode
                    }
                    input
                }

                else -> input
            }

            currentMode.print(inputForMode)
        }
    }
}
