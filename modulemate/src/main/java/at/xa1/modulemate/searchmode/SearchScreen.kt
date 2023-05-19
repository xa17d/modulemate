package at.xa1.modulemate.searchmode

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
import at.xa1.modulemate.ui.reduce
import at.xa1.modulemate.ui.updateHeight
import at.xa1.modulemate.ui.updateItems

fun searchScreen(context: ScreenContext, state: SearchScreenState) = context.printScreen {
    print(state.searchBox, context.size.columns)

    print(state.listBox)

    resetCursor()
    print(CliColor.cursorDown(1) + CliColor.cursorRight(5 + state.searchBox.cursor))
}

data class SearchScreenState(
    val searchBox: TextBox,
    val listBox: ListBox<Module>,
)

fun SearchScreenState.reduce(input: UiUserInput, modules: Modules, height: Int): SearchScreenState {
    return when (input) {
        UiUserInput.Arrow.Down -> copy(listBox = listBox.moveDown())
        UiUserInput.Arrow.Up -> copy(listBox = listBox.moveUp())

        UiUserInput.Escape -> TODO()
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

        else -> {
            copy(searchBox = searchBox.reduce(input)).updateList(modules)
        }
    }.updateHeight(height)
}

private fun SearchScreenState.updateList(modules: Modules): SearchScreenState =
    copy(listBox = listBox.updateItems(modules.allModules.filter { it.path.contains(searchBox.text) }))

internal fun SearchScreenState.updateHeight(height: Int): SearchScreenState =
    copy(listBox = listBox.updateHeight(height - 4))
