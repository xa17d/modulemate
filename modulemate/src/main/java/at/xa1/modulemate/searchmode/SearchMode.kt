package at.xa1.modulemate.searchmode

import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.module.Module
import at.xa1.modulemate.module.ModuleType
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.ui.ListBox
import at.xa1.modulemate.ui.ListBoxItemRenderer
import at.xa1.modulemate.ui.TextBox
import at.xa1.modulemate.ui.Ui

class SearchMode(
    private val ui: Ui,
    private val modules: Modules,
) {
    fun run() {
        var state = SearchScreenState(
            searchBox = TextBox(
                hint = "Search Mode: Type to search modules and commands",
                emoji = "🔍"
            ),
            listBox = ListBox(
                items = modules.allModules,
                height = 0,
                renderer = object : ListBoxItemRenderer<Module> {
                    override fun render(item: Module, isSelected: Boolean): String {
                        val markers = if (modules.isActive(item)) {
                            "  ✅  "
                        } else {
                            "     "
                        }
                        return markers + formatModule(item, !isSelected)
                    }
                }
            )
        )
        while (true) {
            val context = ui.createScreenContext()
            state = state.updateHeight(context.size.rows)
            context.printScreen {
                searchScreen(this, state)
            }
            val input = ui.readUserInput()
            state = state.reduce(input, modules, context.size.rows)
        }
    }
}

// TODO move somewhere appropriate

fun formatModule(module: Module, withColors: Boolean): String {
    val text = module.path

    return if (withColors) {
        val formatting = when (module.type) {
            ModuleType.KOTLIN_LIB -> CliColor.BLUE
            ModuleType.ANDROID_LIB -> CliColor.GREEN
            ModuleType.ANDROID_APP -> CliColor.CYAN
            ModuleType.OTHER -> ""
        }
        return "$formatting${module.path}${CliColor.RESET}"
    } else {
        text
    }
}
