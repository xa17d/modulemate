package at.xa1.modulemate.mode.search

import at.xa1.modulemate.mode.LiveUiMode
import at.xa1.modulemate.mode.ModulesListItemRenderer
import at.xa1.modulemate.mode.SearchListScreen
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput

internal class SearchMode(
    private val ui: Ui,
    private val modules: Modules
) : LiveUiMode {
    private val screen = SearchListScreen(
        emoji = "🔍",
        hint = "Search Mode: Type to search modules",
        listProvider = { filter ->
            val filterTokens = filter.split(' ')
            modules.allModules.filter { module ->
                filterTokens.all { token -> module.path.contains(token) }
            }
        },
        listItemRenderer = ModulesListItemRenderer(modules)
    )

    override fun print(input: UiUserInput?) {
        while (true) {
            screen.print(ui)

            when (val input = ui.readUserInput()) {
                UiUserInput.Tab, UiUserInput.Shift.Tab -> return
                UiUserInput.Return -> selectModule()
                else -> {
                    screen.input(input)
                }
            }
        }
    }

    private fun selectModule() {
        val selectedModule = screen.selectedItem
        val allItemsInList = screen.state.listBox.items

        if (selectedModule != null) {
            if (modules.isActive(selectedModule)) {
                modules.removeRecent(selectedModule)
            } else {
                modules.activate(selectedModule)
            }
        } else {
            val allActive = allItemsInList.all { module -> modules.isActive(module) }
            if (allActive) {
                allItemsInList.forEach { module -> modules.removeRecent(module) }
            } else {
                allItemsInList.forEach { module -> modules.activate(module) }
            }
        }
    }
}
