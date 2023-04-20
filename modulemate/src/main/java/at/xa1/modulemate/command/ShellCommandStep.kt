package at.xa1.modulemate.command

import at.xa1.modulemate.system.Shell
import at.xa1.modulemate.system.isSuccess

class ShellCommandStep(
    override val runWhen: RunWhen,
    private val shell: Shell,
    private val variables: Variables,
    private val command: List<String>
) : CommandStep {
    override fun run(): CommandResult {
        val result = shell.run(
            command.map { variables.replacePlaceholders(it) }.toTypedArray()
        )
        return result.isSuccess.successToCommandResult()
    }
}
