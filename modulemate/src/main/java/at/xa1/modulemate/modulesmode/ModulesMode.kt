package at.xa1.modulemate.modulesmode

import at.xa1.modulemate.liveui.LiveUiMode
import at.xa1.modulemate.liveui.ModulesListItemRenderer
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.ui.ListBox
import at.xa1.modulemate.ui.TextBox
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput

class ModulesMode(
    private val ui: Ui,
    private val modules: Modules
) : LiveUiMode {
    private var state = ModulesScreenState(
        searchBox = TextBox(
            hint = "Module Mode",
            emoji = "📦"
        ),
        listBox = ListBox(
            items = modules.recentModules,
            height = 0,
            renderer = ModulesListItemRenderer(modules)
        )
    )

    override fun print(input: UiUserInput?) {
        val context = ui.createScreenContext()

        if (input != null) {
            state = state.reduce(input, modules, context.size.rows)
        }
        state = state.updateList(modules).updateHeight(context.size.rows)

        context.printScreen {
            modulesScreen(this, state)
        }
    }
}
