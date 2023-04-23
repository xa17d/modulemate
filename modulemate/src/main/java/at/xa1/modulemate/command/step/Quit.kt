package at.xa1.modulemate.command.step

import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandResult

class Quit : CommandStep {
    override fun run(context: CommandContext): CommandResult {
        throw QuitException()
    }
}

class QuitException : Exception()
