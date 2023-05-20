package at.xa1.modulemate.mode

import at.xa1.modulemate.cli.CliColor
import at.xa1.modulemate.ui.ListBox
import at.xa1.modulemate.ui.ListItemRenderer
import at.xa1.modulemate.ui.ScreenContext
import at.xa1.modulemate.ui.TextBox
import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput
import at.xa1.modulemate.ui.moveDown
import at.xa1.modulemate.ui.moveUp
import at.xa1.modulemate.ui.print
import at.xa1.modulemate.ui.reduce
import at.xa1.modulemate.ui.selectedItemOrNull
import at.xa1.modulemate.ui.updateHeight
import at.xa1.modulemate.ui.updateItems

class SearchListScreen<T>(
    emoji: String,
    hint: String,
    private val listProvider: (filter: String) -> List<T>,
    listItemRenderer: ListItemRenderer<T>
) : Screen<SearchListScreenState<T>>(
    initialState = SearchListScreenState(
        searchBox = TextBox(hint = hint, emoji = emoji),
        listBox = ListBox(items = listProvider(""), height = 0, itemRenderer = listItemRenderer)
    ),
    printer = SearchListScreenPrinter()
) {
    fun input(input: UiUserInput) {
        update { old -> old.reduce(input, listProvider) }
    }

    override fun onPrint(context: ScreenContext) {
        update { old -> old.updateHeight(context.size.rows) }
    }

    val selectedItem: T?
        get() = state.listBox.selectedItemOrNull()

    fun moveCursorAfterTextBox(ui: Ui) {
        val context = ui.createScreenContext()

        context.resetCursor()
        context.print(CliColor.cursorDown(3) + CliColor.CLEAR_UNTIL_END_OF_SCREEN)
        context.flush()
    }

    fun printOnlyTextBox(ui: Ui, hintOverride: String? = null) {
        val context = ui.createScreenContext()

        val textBox = state.searchBox.copy(hint = hintOverride ?: state.searchBox.hint)

        context.print("\n")
        context.print(textBox, context.size.columns)
        context.flush()
        print(CliColor.cursorUp(2) + CliColor.cursorRight(5 + textBox.cursor))
        context.flush()
    }
}

data class SearchListScreenState<T>(
    val searchBox: TextBox,
    val listBox: ListBox<T>
)

class SearchListScreenPrinter<T> : ScreenPrinter<SearchListScreenState<T>> {
    override fun print(context: ScreenContext, state: SearchListScreenState<T>) = context.printScreen {
        print(state.searchBox, size.columns)

        print(state.listBox)

        resetCursor()
        print(CliColor.cursorDown(1) + CliColor.cursorRight(5 + state.searchBox.cursor))
    }
}

fun <T> SearchListScreenState<T>.reduce(
    input: UiUserInput,
    listProvider: (filter: String) -> List<T>
): SearchListScreenState<T> {
    return when (input) {
        UiUserInput.Arrow.Down -> copy(listBox = listBox.moveDown())
        UiUserInput.Arrow.Up -> copy(listBox = listBox.moveUp())
        else -> {
            copy(searchBox = searchBox.reduce(input)).run {
                updateList(listProvider(searchBox.text))
            }
        }
    }
}

private fun <T> SearchListScreenState<T>.updateList(newItems: List<T>): SearchListScreenState<T> =
    copy(listBox = listBox.updateItems(newItems))

private fun <T> SearchListScreenState<T>.updateHeight(newHeight: Int): SearchListScreenState<T> =
    copy(listBox = listBox.updateHeight(newHeight - 4))
