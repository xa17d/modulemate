package at.xa1.modulemate.command

sealed interface CommandStep {
    fun run(): CommandResult
}
