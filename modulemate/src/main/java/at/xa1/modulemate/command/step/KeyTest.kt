package at.xa1.modulemate.command.step

import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandResult
import at.xa1.modulemate.ui.Ui

class KeyTest(
    private val getUi: () -> Ui,
) : CommandStep {
    override fun run(context: CommandContext): CommandResult {
        val ui = getUi()

        while (true) {
            val userInput = ui.readUserInput()
            println(userInput)
        }
    }

    companion object {
        val SHORTCUTS: List<String> = listOf("keytest")
    }
}
