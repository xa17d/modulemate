package at.xa1.modulemate.command.step

import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandResult

sealed interface CommandStep {
    fun run(context: CommandContext): CommandResult
}
