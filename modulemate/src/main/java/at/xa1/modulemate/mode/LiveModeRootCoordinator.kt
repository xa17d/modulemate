package at.xa1.modulemate.mode

import at.xa1.modulemate.UserCommandRunner
import at.xa1.modulemate.command.CommandList
import at.xa1.modulemate.mode.command.CommandModeCoordinator
import at.xa1.modulemate.mode.flash.FlashModeCoordinator
import at.xa1.modulemate.mode.help.HelpModeCoordinator
import at.xa1.modulemate.mode.modules.ModulesModeCoordinator
import at.xa1.modulemate.mode.search.SearchModeCoordinator
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput

internal class LiveModeRootCoordinator(
    ui: Ui,
    private val modules: Modules,
    commandList: CommandList,
    commandRunner: UserCommandRunner,
) {
    private val helpMode = HelpModeCoordinator(ui)
    private val searchMode = SearchModeCoordinator(ui, modules)
    private val modulesMode = ModulesModeCoordinator(ui, modules)
    private val commandMode = CommandModeCoordinator(ui, commandList, commandRunner)
    private val flashMode = FlashModeCoordinator(ui, commandList, commandRunner)
    private val modes =
        listOf(
            helpMode,
            searchMode,
            modulesMode,
            commandMode,
            flashMode,
        )

    private var currentMode: ModeCoordinator = helpMode

    private fun moveMode(delta: Int) {
        val currentModeIndex = modes.indexOf(currentMode)
        val nextIndex = (currentModeIndex + delta).mod(modes.size)
        currentMode = modes[nextIndex]
    }

    fun run() {
        if (modules.activeModules.isNotEmpty()) {
            currentMode = modulesMode
        }

        while (true) {
            when (val input = currentMode.run()) {
                UiUserInput.Tab -> {
                    moveMode(1)
                }

                UiUserInput.Shift.Tab -> {
                    moveMode(-1)
                }

                is UiUserInput.Char -> {
                    searchMode.setSearchText(input.char.toString())
                    currentMode = searchMode
                }

                is UiUserInput.Escape -> {
                    return
                }
                else -> {
                    // Ignore.
                }
            }
        }
    }
}
