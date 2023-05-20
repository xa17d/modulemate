package at.xa1.modulemate.mode.modules

import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.module.Module
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.ui.ListBox
import at.xa1.modulemate.ui.ScreenContext
import at.xa1.modulemate.ui.TextBox
import at.xa1.modulemate.ui.UiUserInput
import at.xa1.modulemate.ui.moveDown
import at.xa1.modulemate.ui.moveUp
import at.xa1.modulemate.ui.print
import at.xa1.modulemate.ui.selectedItemOrNull
import at.xa1.modulemate.ui.updateHeight
import at.xa1.modulemate.ui.updateItems

fun modulesScreen(context: ScreenContext, state: ModulesScreenState) = context.printScreen {
    print(state.searchBox, context.size.columns)

    print(state.listBox)

    resetCursor()
    print(CliColor.cursorDown(1) + CliColor.cursorRight(5 + state.searchBox.cursor))
}

data class ModulesScreenState(
    val searchBox: TextBox,
    val listBox: ListBox<Module>
)

fun ModulesScreenState.reduce(input: UiUserInput, modules: Modules, height: Int): ModulesScreenState {
    return when (input) {
        UiUserInput.Arrow.Down -> copy(listBox = listBox.moveDown())
        UiUserInput.Arrow.Up -> copy(listBox = listBox.moveUp())

        UiUserInput.Return -> {
            if (listBox.selectedIndex != -1) {
                val module = listBox.items[listBox.selectedIndex]
                if (modules.isActive(module)) {
                    modules.deactivate(module)
                } else {
                    modules.activate(module)
                }
            } else {
                val allActive = listBox.items.all { module -> modules.isActive(module) }
                if (allActive) {
                    listBox.items.forEach { module -> modules.deactivate(module) }
                } else {
                    listBox.items.forEach { module -> modules.activate(module) }
                }
            }
            this
        }

        UiUserInput.Backspace, UiUserInput.Delete -> {
            val module = listBox.selectedItemOrNull()
            if (module != null) {
                modules.removeRecent(module)
            }
            this
        }

        else -> this
    }.updateList(modules).updateHeight(height)
}

internal fun ModulesScreenState.updateList(modules: Modules): ModulesScreenState =
    copy(listBox = listBox.updateItems(modules.recentModules))

internal fun ModulesScreenState.updateHeight(height: Int): ModulesScreenState =
    copy(listBox = listBox.updateHeight(height - 4))
