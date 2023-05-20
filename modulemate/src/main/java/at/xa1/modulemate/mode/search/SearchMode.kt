package at.xa1.modulemate.mode.search

import at.xa1.modulemate.mode.LiveUiMode
import at.xa1.modulemate.mode.ModulesListItemRenderer
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.ui.ListBox
import at.xa1.modulemate.ui.TextBox
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput

class SearchMode(
    private val ui: Ui,
    private val modules: Modules
) : LiveUiMode {
    private var state = SearchScreenState(
        searchBox = TextBox(
            hint = "Search Mode: Type to search modules and commands",
            emoji = "🔍"
        ),
        listBox = ListBox(
            items = modules.allModules,
            height = 0,
            itemRenderer = ModulesListItemRenderer(modules)
        )
    )

    override fun print(input: UiUserInput?) {
        val context = ui.createScreenContext()

        state = if (input != null) {
            state.reduce(input, modules, context.size.rows)
        } else {
            state.updateHeight(context.size.rows)
        }

        context.printScreen {
            searchScreen(this, state)
        }
    }
}
