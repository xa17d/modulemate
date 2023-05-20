package at.xa1.modulemate.liveui

import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.searchmode.SearchMode
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput

class LiveUi(
    private val ui: Ui,
    modules: Modules
) {
    private val searchMode = SearchMode(ui, modules)

    private val modes = listOf(searchMode, TestMode(ui))

    private var currentMode: LiveUiMode = searchMode

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

                else -> input
            }

            currentMode.print(inputForMode)
        }
    }
}
