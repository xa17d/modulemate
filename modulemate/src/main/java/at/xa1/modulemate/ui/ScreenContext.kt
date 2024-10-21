package at.xa1.modulemate.ui

import at.xa1.modulemate.cli.CliFormat
import org.jline.terminal.Size
import org.jline.terminal.Terminal

class ScreenContext(terminal: Terminal) {
    private val writer = terminal.writer()

    val size: Size = terminal.size

    fun resetCursor() {
        print(CliFormat.cursorLeft(size.columns) + CliFormat.cursorUp(size.rows))
    }

    fun clear() {
        resetCursor()
        print(CliFormat.CLEAR_ENTIRE_SCREEN)
    }

    fun print(s: String) {
        writer.print(s)
    }

    fun flush() {
        writer.flush()
    }

    inline fun printScreen(
        clear: Boolean = true,
        block: ScreenContext.() -> Unit,
    ) {
        if (clear) {
            this.clear()
        }

        this.block()

        flush()
    }
}
