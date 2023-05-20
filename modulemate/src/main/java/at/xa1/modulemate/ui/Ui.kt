package at.xa1.modulemate.ui

import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder

class Ui private constructor(
    private val terminal: Terminal
) {
    fun createScreenContext(): ScreenContext = ScreenContext(terminal)

    fun readUserInput(): UiUserInput = readUserInput(terminal.reader())

    companion object {
        fun init(): Ui {
            val terminal = TerminalBuilder.builder()
                .build()

            terminal.enterRawMode()
            terminal.echo(false)

            return Ui(terminal)
        }
    }
}
