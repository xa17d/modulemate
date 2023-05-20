package at.xa1.modulemate.ui

import at.xa1.modulemate.cli.CliEmoji
import at.xa1.modulemate.cli.CliFormat
import sun.misc.Signal
import kotlin.system.exitProcess

/**
 * Clears the screen when Control+C is pressed (SIGINT)
 */
class CleanExit {
    fun setup(ui: Ui) {
        Signal.handle(Signal("INT")) { // SIGINT
            val context = ui.createScreenContext()
            context.print(
                CliFormat.cursorDown(2) + CliFormat.cursorLeft(context.size.columns) +
                    CliFormat.CLEAR_UNTIL_END_OF_SCREEN
            )
            context.flush()
            context.print("${CliEmoji.WAVING_HAND} bye\n")
            context.flush()
            exitProcess(0)
        }
    }
}
