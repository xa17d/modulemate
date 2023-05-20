package at.xa1.modulemate.mode

import at.xa1.modulemate.ui.ScreenContext
import at.xa1.modulemate.ui.Ui

open class Screen<State>(
    initialState: State,
    private val printer: ScreenPrinter<State>
) {
    var state: State = initialState
    inline fun update(crossinline transform: (old: State) -> State) {
        state = transform(state)
    }

    protected open fun onPrint(context: ScreenContext) {
    }
    fun print(ui: Ui) {
        val context = ui.createScreenContext()
        onPrint(context)
        printer.print(context, state)
    }
}

interface ScreenPrinter<State> {
    fun print(context: ScreenContext, state: State)
}
