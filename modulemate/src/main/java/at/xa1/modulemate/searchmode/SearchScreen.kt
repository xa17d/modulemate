package at.xa1.modulemate.searchmode

import at.xa1.modulemate.ui.ScreenContext
import at.xa1.modulemate.ui.textBox

fun searchScreen(context: ScreenContext, state: SearchScreenState) = context.printScreen {
    textBox(
        text = state.input,
        hint = "Prompt Mode: Type to search modules and commands",
        emoji = "🔍",
        width = size.columns
    )

    setCursor(5 + state.input.length, 1)
}

data class SearchScreenState(val input: String)
