package at.xa1.modulemate.ui

import at.xa1.modulemate.cli.CliColor
import org.jline.terminal.Size
import org.jline.terminal.Terminal

class ScreenContext(terminal: Terminal) {

    private val writer = terminal.writer()

    val size: Size = terminal.size
    fun resetCursor() {
        print(CliColor.cursorLeft(size.columns) + CliColor.cursorUp(size.rows))
    }

    fun clear() {
        resetCursor()
        print(CliColor.CLEAR_ENTIRE_SCREEN)
    }

    fun print(s: String) {
        writer.print(s)
    }

    fun flush() {
        writer.flush()
    }

    inline fun printScreen(clear: Boolean = true, block: ScreenContext.() -> Unit) {
        if (clear) {
            this.clear()
        }

        this.block()

        flush()
    }
}
