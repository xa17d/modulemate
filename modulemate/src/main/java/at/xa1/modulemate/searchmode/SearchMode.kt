package at.xa1.modulemate.searchmode

import at.xa1.modulemate.ui.TextBox
import at.xa1.modulemate.ui.Ui

class SearchMode(private val ui: Ui) {
    fun run() {
        var state = SearchScreenState(
            searchBox = TextBox(
                hint = "Search Mode: Type to search modules and commands",
                emoji = "🔍"
            )
        )
        while (true) {
            val context = ui.createScreenContext()
            context.printScreen {
                searchScreen(this, state)
            }
            val input = ui.readUserInput()
            state = state.reduce(input)
        }
    }
}
