package at.xa1.modulemate.mode.search

import at.xa1.modulemate.cli.CliEmoji
import at.xa1.modulemate.mode.ModeCoordinator
import at.xa1.modulemate.mode.ModulesListItemRenderer
import at.xa1.modulemate.mode.SearchListScreen
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput

internal class SearchModeCoordinator(
    private val ui: Ui,
    private val modules: Modules,
) : ModeCoordinator {
    private val screen =
        SearchListScreen(
            emoji = CliEmoji.MAGNIFYING_GLASS.toString(),
            hint = "Search Mode: Type to search modules",
            listProvider = { filter ->
                val filterTokens = filter.split(' ')
                modules.allModules.filter { module ->
                    filterTokens.all { token -> module.path.contains(token) }
                }
            },
            listItemRenderer = ModulesListItemRenderer(modules),
        )

    fun setSearchText(searchText: String) {
        screen.update { old ->
            old.copy(
                searchBox =
                    old.searchBox.copy(
                        text = searchText,
                        cursor = searchText.length,
                    ),
            )
        }
    }

    override fun run(): UiUserInput {
        while (true) {
            screen.print(ui)

            when (val input = ui.readUserInput()) {
                UiUserInput.Tab, UiUserInput.Shift.Tab, UiUserInput.Escape -> return input
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
