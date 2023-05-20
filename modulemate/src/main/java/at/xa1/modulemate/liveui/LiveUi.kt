package at.xa1.modulemate.liveui

import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.searchmode.SearchMode
import at.xa1.modulemate.ui.Ui

class LiveUi(
    private val ui: Ui,
    modules: Modules
) {
    private val searchMode = SearchMode(ui, modules)
    fun run() {
        searchMode.print(null)
        while (true) {
            val input = ui.readUserInput()
            searchMode.print(input)
        }
    }
}
