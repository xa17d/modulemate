package at.xa1.modulemate.command.step

import at.xa1.modulemate.command.CommandResult

sealed interface CommandStep {
    fun run(): CommandResult
}
