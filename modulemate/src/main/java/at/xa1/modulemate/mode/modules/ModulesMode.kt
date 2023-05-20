package at.xa1.modulemate.mode.modules

import at.xa1.modulemate.mode.LiveUiMode
import at.xa1.modulemate.mode.ModulesListItemRenderer
import at.xa1.modulemate.mode.SearchListScreen
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput

internal class ModulesMode(
    private val ui: Ui,
    private val modules: Modules
) : LiveUiMode {
    private val screen = SearchListScreen(
        emoji = "📦",
        hint = "Module Mode",
        listProvider = { modules.recentModules },
        listItemRenderer = ModulesListItemRenderer(modules)
    )

    override fun print(input: UiUserInput?) {
        while (true) {
            screen.print(ui)

            when (val input = ui.readUserInput()) {
                UiUserInput.Tab, UiUserInput.Shift.Tab, is UiUserInput.Char -> return
                UiUserInput.Return -> toggleModule()
                UiUserInput.Backspace, UiUserInput.Delete -> removeRecentModule()
                else -> {
                    screen.input(input)
                }
            }
        }
    }

    private fun removeRecentModule() {
        val module = screen.selectedItem
        if (module != null) {
            modules.removeRecent(module)
        }
    }

    private fun toggleModule() {
        val selectedModule = screen.selectedItem
        val allItemsInList = screen.state.listBox.items

        if (selectedModule != null) {
            if (modules.isActive(selectedModule)) {
                modules.deactivate(selectedModule)
            } else {
                modules.activate(selectedModule)
            }
        } else {
            val allActive = allItemsInList.all { module -> modules.isActive(module) }
            if (allActive) {
                allItemsInList.forEach { module -> modules.deactivate(module) }
            } else {
                allItemsInList.forEach { module -> modules.activate(module) }
            }
        }
    }
}
