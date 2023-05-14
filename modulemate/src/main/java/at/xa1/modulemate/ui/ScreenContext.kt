package at.xa1.modulemate.ui

import at.xa1.modulemate.cli.CliColor
import org.jline.terminal.Size
import org.jline.terminal.Terminal

class ScreenContext(private val terminal: Terminal) {

    private val writer = terminal.writer()

    val size: Size = terminal.size
    fun setCursor(newX: Int, newY: Int) {
        val cursor = terminal.getCursorPosition { error("Internal Assumption Error: Discarded chars! $it") }

        val x = newX.coerceIn(0, size.columns - 1)
        val y = newY.coerceIn(0, size.rows - 1)

        val dx = x - cursor.x
        val dy = y - cursor.y

        when {
            dx < 0 -> print(CliColor.cursorLeft(-dx))
            dx > 0 -> print(CliColor.cursorRight(dx))
        }

        when {
            dy < 0 -> print(CliColor.cursorUp(-dy))
            dy > 0 -> print(CliColor.cursorDown(dy))
        }
    }

    fun clear() {
        print(CliColor.CLEAR_ENTIRE_SCREEN)
        setCursor(0, 0)
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
